package itmo.escience.simenv.simulator

import java.io.File

import itmo.escience.simenv.algorithms.Scheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.simulator.events.{InitEvent, TaskStarted, _}
import itmo.escience.simenv.utilities.SimLogger
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.{Marker, MarkerManager, Logger, LogManager}

import scala.util.Random


/**
  * Perform discrete event-drivent simulation of workflows execution
  * @param scheduler algorithm for scheduling, must implement Scheduler interface
  * @param ctx contains description of computational environments and may perform actions on it
  */
class BasicSimulator[T <: Task, N <: Node](val scheduler: Scheduler, var ctx: Context[T, N]) extends Simulator[T, N] {

  val queue = new EventQueue()
  val rnd = new Random()
  SimLogger.setCtx(ctx.asInstanceOf[Context[DaxTask, CapacityBasedNode]])

  /**
    * generates and adds the very first event [[InitEvent]] to the event queue
    * Scheduling for initial state of environment have to be placed in the handler of this event
    */
  def init() = {
    queue.submitEvent(InitEvent.instance)
    SimLogger.log("Init event submitted")
  }

  /**
    * starts the simulation.
    * The simulation will finish when there is not any event in the queue
    */
  override def runSimulation(): Unit = {

    while (!queue.isEmpty) {
      val event = queue.next()
      SimLogger.logEvent(event)
      dispatchEvent(event)
      SimLogger.log("Event has been handled")
    }
    SimLogger.log("Finished")
    SimLogger.logSched(ctx.schedule.asInstanceOf[Schedule[DaxTask, CapacityBasedNode]])
  }

  /**
    * chooses an appropriate event handler for the current event
    * @param event
    */
  def dispatchEvent(event: Event): Unit = event match {
    case InitEvent => onInitEvent()
    case ev: TaskStarted => onTaskStarted(ev)
    case ev: TaskFinished => onTaskFinished(ev)
    case ev: TaskFailed => onTaskFailed(ev)
    case _ => throw new Exception(s"Unknown type of the event: ${event.getClass}")
  }

  def onInitEvent() = {
    SimLogger.log("Init event")
    val schedule = scheduler.schedule(ctx, ctx.environment)
    SimLogger.log("Init schedule is generated")
    // This function applies new schedule and generates events
    ctx.applySchedule(schedule, queue)
    SimLogger.logSched(ctx.schedule.asInstanceOf[Schedule[DaxTask, CapacityBasedNode]])

    SimLogger.log("Init schedule has been applied")
    // Generate initial events
    init_helper()
    SimLogger.log("Initial task has been handled")
  }

  def init_helper() = {
    val schedMap = ctx.schedule.getMap
    for (n <- ctx.environment.nodes) {
      //TODO try to remove "asInstanceOf"
      if (!schedMap.containsKey(n.id)) {
        ctx.schedule.addNode(n.id)
      }
      if (schedMap.get(n.id).nonEmpty) {
        val firstItem = schedMap.get(n.id).head.asInstanceOf[TaskScheduleItem[T, N]]
        queue.submitEvent(new TaskStarted(id = firstItem.id, name = firstItem.name,
          postTime = ctx.currentTime, eventTime = firstItem.startTime,
          task = firstItem.task, node = firstItem.node))
      }
    }
  }

  private def onTaskStarted(event: TaskStarted) = {
    //TODO: add logging here
    ctx.setTime(event.eventTime)
    val nid = event.node.id
    val schedMap = ctx.schedule.getMap
    val nodeSched = schedMap.get(nid)
    val (iterator, curItem, counter) = ctx.schedule.findItemInNodeSched(nid, event.id)
    schedMap.put(nid, nodeSched.take(counter))
    schedMap.get(nid).add(curItem.asInstanceOf[TaskScheduleItem[T, N]].changeStatus(ScheduleItemStatus.RUNNING))
    taskFailer(curItem.asInstanceOf[TaskScheduleItem[T, N]])
    while (iterator.hasNext) {
      schedMap.get(nid).add(iterator.next())
    }
  }

  private def onTaskFinished(event: TaskFinished) = {
    ctx.setTime(event.eventTime)
    //TODO: add logging here
    // 1) set status on finished
    val nid = event.node.id
    val schedMap = ctx.schedule.getMap
    val nodeSched = schedMap.get(nid)
    val (iterator, curItem, counter) = ctx.schedule.findItemInNodeSched(nid, event.id)

    schedMap.put(nid, nodeSched.take(counter))
    val finishedItem = curItem.asInstanceOf[TaskScheduleItem[T, N]].changeStatus(ScheduleItemStatus.FINISHED)
    schedMap.get(nid).add(finishedItem)

    if (iterator.hasNext) {
      val nextItem = iterator.next().asInstanceOf[TaskScheduleItem[T, N]]
      queue.submitEvent(new TaskStarted(id = nextItem.id, name = nextItem.name,
        postTime = ctx.currentTime, eventTime = nextItem.startTime,
        task = nextItem.task, node = nextItem.node))
      schedMap.get(nid).add(nextItem)
    }
    while (iterator.hasNext) {
      schedMap.get(nid).add(iterator.next())
    }
  }

  def onTaskFailed(event: TaskFailed) = {
    task_failed_before(event)

    // Reschedule
    val sc = scheduler.schedule(ctx, ctx.environment)
    queue.eq = queue.eq.filter(x => !x.isInstanceOf[TaskStarted])
    // Apply new schedule
    ctx.applySchedule(sc, queue)
    SimLogger.log("Rescheduling has been completed")
    SimLogger.logSched(sc.asInstanceOf[Schedule[DaxTask, CapacityBasedNode]])

    // submit new events, if it is required after the failed task
    task_failed_after()

  }

  def task_failed_before(event: TaskFailed) = {
    ctx.setTime(event.eventTime)

    // Mark this item as failed
    val nid = event.node.id
    val schedMap = ctx.schedule.getMap
    val nodeSched = schedMap.get(nid)
    val (iterator, curItem, counter) = ctx.schedule.findItemInNodeSched(nid, event.id)

    schedMap.put(nid, nodeSched.take(counter))
    val finishedItem = curItem.asInstanceOf[TaskScheduleItem[T, N]].setToFailed(event.eventTime)
    schedMap.get(nid).add(finishedItem)

    while (iterator.hasNext) {
      schedMap.get(nid).add(iterator.next())
    }
  }

  def task_failed_after() = {
    val newFixedSchedule = ctx.schedule.fixedSchedule()
    for (nid <- newFixedSchedule.nodeIds()) {
      val fixNodeSched = newFixedSchedule.getMap.get(nid)
      if (fixNodeSched.nonEmpty && fixNodeSched.last.status != ScheduleItemStatus.RUNNING) {
        val lastItem = fixNodeSched.last
        val (newIterator, newCurItem, newCounter) = ctx.schedule.findItemInNodeSched(nid, lastItem.id)
        if (newIterator.hasNext) {
          val nextItem = newIterator.next().asInstanceOf[TaskScheduleItem[T, N]]
          if (!queue.eq.map(x => x.id).toList.contains(nextItem.id)) {
            queue.submitEvent(new TaskStarted(id = nextItem.id, name = nextItem.name,
              postTime = ctx.currentTime, eventTime = nextItem.startTime,
              task = nextItem.task, node = nextItem.node))
          }
        }
      } else {
        val newNodeSched = ctx.schedule.getMap.get(nid)
        if (newNodeSched.nonEmpty) {
          val firstItem = newNodeSched.head.asInstanceOf[TaskScheduleItem[T, N]]
          if (firstItem.status == ScheduleItemStatus.UNSTARTED && !queue.eq.map(x => x.id).toList.contains(firstItem.id)) {
            queue.submitEvent(new TaskStarted(id = firstItem.id, name = firstItem.name,
              postTime = ctx.currentTime, eventTime = firstItem.startTime,
              task = firstItem.task, node = firstItem.node))
          }
        }
      }
    }
  }

  private def taskFailer(taskScheduleItem: TaskScheduleItem[T, N]) = {
    //TODO: add logging here
    //    taskScheduleItem.status = TaskScheduleItemStatus.RUNNING
    val dice = rnd.nextDouble()
    // TODO reliablity not via CpuTimeNode
    if (dice < taskScheduleItem.node.asInstanceOf[CapacityBasedNode].reliability) {
      // Task will be finished
      val taskFinishedEvent = new TaskFinished(id = taskScheduleItem.id, name = taskScheduleItem.name, postTime = ctx.currentTime,
        eventTime = taskScheduleItem.endTime, taskScheduleItem.task, taskScheduleItem.node)
      queue.submitEvent(taskFinishedEvent)
    } else {
      // Task will be failed (random time between start and end of the current schedule item)
      val itemStart = taskScheduleItem.startTime
      val itemEnd = taskScheduleItem.endTime
      val failTime = rnd.nextDouble() * (itemEnd - itemStart) + itemStart
      val taskFailedEvent = new TaskFailed(id = taskScheduleItem.id, name = taskScheduleItem.name, postTime = ctx.currentTime,
        eventTime = failTime, taskScheduleItem.task, taskScheduleItem.node)
      queue.submitEvent(taskFailedEvent)
    }
  }
}


