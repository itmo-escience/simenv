package itmo.escience.simenv.simulator


import itmo.escience.simenv.algorithms.Scheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.simulator.events.{InitEvent, TaskStarted, _}
import org.apache.logging.log4j.{Level, LogManager, Logger}

import scala.util.Random


/**
 * Perform discrete event-drivent simulation of workflows execution
 * @param scheduler algorithm for scheduling, must implement Scheduler interface
 * @param ctx contains description of computational environments and may perform actions on it
 */
class SimpleSimulator(val scheduler: Scheduler[DaxTask, CapacityBasedNode], var ctx:Context[DaxTask, CapacityBasedNode]) extends Simulator {

  val logger: Logger = LogManager.getLogger("logger")
  val queue = new EventQueue()
  val rnd = new Random()

  /**
   * generates and adds the very first event [[InitEvent]] to the event queue
   * Scheduling for initial state of environment have to be placed in the handler of this event
   */
  def init() = {
    queue.submitEvent(InitEvent.instance)
    logger.trace("Init event submitted")
  }

  /**
   * starts the simulation.
   * The simulation will finish when there is not any event in the queue
   */
  override def runSimulation(): Unit = {
    while (!queue.isEmpty) {
      println(queue.print())
      val event = queue.next()
      dispatchEvent(event)
      println("---next event---")
      print(s"current time ${ctx.currentTime}")
      println(s"event time ${event.eventTime}")
      println(ctx.schedule.prettyPrint())
    }
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

  private def onInitEvent() = {

    // for any event there exists a general sequence of steps
    // 1. update context according to the event
    // 2. update CURRENRT schedule according to the event
    // 3. check if the situation needs rescheduling: yes - 4, no - 6
    // 4. create new schedule (or run background operations and etc)
    // 5. apply new schedule to eventQueue (if any) and create new context
    // 6. Exit

    //val schedule = scheduler.schedule(ctx)
    //TODO: add logging here
    logger.trace("Init event")
    val schedule = scheduler.schedule(ctx)
    logger.trace("Init schedule is generated")
    // This function applies new schedule and generates events
    ctx.applySchedule(schedule, queue)
    logger.trace("Init schedule has been applied")
    // Generate initial events
    val schedMap = ctx.schedule.getMap()
    for (n <- ctx.environment.nodes) {
      //TODO try to remove "asInstanceOf"
      val firstItem = schedMap.get(n.id).head.asInstanceOf[TaskScheduleItem]
      queue.submitEvent(new TaskStarted(id=firstItem.id, name=firstItem.name,
        postTime=ctx.currentTime, eventTime=firstItem.startTime,
        task=firstItem.task, node=firstItem.node))
    }
    logger.trace("Initial task has been handled")
  }

  private def onTaskStarted(event: TaskStarted) = {
    //TODO: add logging here
    logger.trace(s"Task has been started ${event.task.name}")
    ctx.setTime(event.eventTime)
    val nid = event.node.id
    val schedMap = ctx.schedule.getMap()
    val nodeSched = schedMap.get(nid)
    val (iterator, curItem, counter) = ctx.schedule.findItemInNodeSched(nid, event.id)
    schedMap.put(nid, nodeSched.take(counter))
    schedMap.get(nid).add(curItem.asInstanceOf[TaskScheduleItem].changeStatus(TaskScheduleItemStatus.RUNNING))
    taskFailer(curItem.asInstanceOf[TaskScheduleItem])
    while (iterator.hasNext) {
      schedMap.get(nid).add(iterator.next())
    }
  }

  private def onTaskFinished(event: TaskFinished) = {
    ctx.setTime(event.eventTime)
    //TODO: add logging here
    // 1) set status on finished
    val nid = event.node.id
    val schedMap = ctx.schedule.getMap()
    val nodeSched = schedMap.get(nid)
    val (iterator, curItem, counter) = ctx.schedule.findItemInNodeSched(nid, event.id)

    schedMap.put(nid, nodeSched.take(counter))
    val finishedItem = curItem.asInstanceOf[TaskScheduleItem].changeStatus(TaskScheduleItemStatus.FINISHED)
    schedMap.get(nid).add(finishedItem)

    if (iterator.hasNext) {
      val nextItem = iterator.next().asInstanceOf[TaskScheduleItem]
      queue.submitEvent(new TaskStarted(id=nextItem.id, name=nextItem.name,
        postTime=ctx.currentTime, eventTime=nextItem.startTime,
        task=nextItem.task, node=nextItem.node))
      schedMap.get(nid).add(nextItem)
    }
    while (iterator.hasNext) {
      schedMap.get(nid).add(iterator.next())
    }
  }

  private def onTaskFailed(event: TaskFailed) = {
    //TODO: add logging here
    // Mark this item as failed
    // Reschedule
    // Apply new schedule
    throw new NotImplementedError()
  }

  private def taskFailer(taskScheduleItem: TaskScheduleItem) = {
    //TODO: add logging here
//    taskScheduleItem.status = TaskScheduleItemStatus.RUNNING
    val dice = rnd.nextDouble()
    if (dice < taskScheduleItem.node.reliability) {
      // Task will be finished
      val taskFinishedEvent = new TaskFinished(id=taskScheduleItem.id, name=taskScheduleItem.name, postTime=ctx.currentTime,
        eventTime = taskScheduleItem.endTime, taskScheduleItem.task, taskScheduleItem.node)
      queue.submitEvent(taskFinishedEvent)
    } else {
      // Task will be failed (random time between start and end of the current schedule item)
      val itemStart = taskScheduleItem.startTime
      val itemEnd = taskScheduleItem.endTime
      val failTime = rnd.nextDouble() * (itemEnd - itemStart) + itemStart
      val taskFailedEvent = new TaskFailed(id=taskScheduleItem.id, name=taskScheduleItem.name, postTime=ctx.currentTime,
        eventTime = failTime, taskScheduleItem.task, taskScheduleItem.node)
      queue.submitEvent(taskFailedEvent)
    }
  }
}


