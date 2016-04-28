package itmo.escience.simenv.simulator


import itmo.escience.simenv.algorithms.Scheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.CarrierNodeEnvironment
import itmo.escience.simenv.simulator.events.Rescheduling
import itmo.escience.simenv.simulator.events.{InitEvent, TaskStarted, _}
import itmo.escience.simenv.utilities.SimLogger
import itmo.escience.simenv.utilities.ScheduleVisualizer

import scala.util.Random


/**
  * Perform discrete event-drivent simulation of workflows execution
  *
  * @param scheduler algorithm for scheduling, must implement Scheduler interface
  * @param ctx contains description of computational environments and may perform actions on it
  */
class BasicSimulator[T <: Task, N <: Node](val scheduler: Scheduler, var ctx: Context[T, N],
                                           val nodeDownTime: Double, val resDownTime: Double) extends Simulator[T, N] {

  val queue = new EventQueue()
  val rnd = new Random()
  SimLogger.setCtx(ctx.asInstanceOf[Context[DaxTask, CapacityBasedNode]])
  val vis = new ScheduleVisualizer[T, N]

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

    vis.drawSched(ctx.schedule, ctx.environment.asInstanceOf[CarrierNodeEnvironment[CapacityBasedNode]])
  }

  /**
    * chooses an appropriate event handler for the current event
    *
    * @param event
    */
  def dispatchEvent(event: Event): Unit = event match {
    case InitEvent => onInitEvent()
    case ev: TaskStarted => onTaskStarted(ev)
    case ev: TaskFinished => onTaskFinished(ev)
    case ev: TaskFailed => onTaskFailed(ev)
//    case ev: NodeFailed => onNodeFailed(ev)
//    case ev: NodeUpped => onNodeUpped(ev)
    case ev: Rescheduling => onRescheduling(ev)
    case _ => throw new Exception(s"Unknown type of the event: ${event.getClass}")
  }

  def onInitEvent() = {
    SimLogger.log("Init event")
    val schedule = scheduler.schedule(ctx, ctx.environment)
    SimLogger.log("Init schedule is generated")
    // This function applies new schedule and generates events
    ctx.applySchedule(schedule, queue)
    SimLogger.logSched(ctx.schedule.asInstanceOf[Schedule[DaxTask, CapacityBasedNode]])

    vis.drawSched(ctx.schedule, ctx.environment.asInstanceOf[CarrierNodeEnvironment[CapacityBasedNode]])

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
    queue.submitEvent(new Rescheduling(id="reschedule", name="reschedule", postTime = ctx.currentTime, eventTime = ctx.currentTime))
  }

  def onRescheduling(event: Rescheduling) = {
    ctx.setTime(event.eventTime)
    // Reschedule

      if (ctx.schedule.restTasks().nonEmpty) {

        val sc = scheduler.schedule(ctx, ctx.environment)
        queue.eq = queue.eq.filter(x => !x.isInstanceOf[TaskStarted])
        // Apply new schedule
        ctx.applySchedule(sc, queue)

        vis.drawSched(ctx.schedule, ctx.environment.asInstanceOf[CarrierNodeEnvironment[CapacityBasedNode]])


        SimLogger.log("Rescheduling has been completed")
        SimLogger.logSched(sc.asInstanceOf[Schedule[DaxTask, CapacityBasedNode]])

        // submit new events, if it is required after the failed task
        task_failed_after()
      }
  }

  def task_failed_before(event: TaskFailed) = {
    ctx.setTime(event.eventTime)

    // Mark this item as failed
    val nid = event.node.id
    val schedMap = ctx.schedule.getMap
    val nodeSched = schedMap.get(nid)
    val (iterator, curItem, counter) = ctx.schedule.findItemInNodeSched(nid, event.id)

    schedMap.put(nid, nodeSched.take(counter))
    val finishedItem = curItem.asInstanceOf[TaskScheduleItem[T, N]].
      setToFailed(event.eventTime + nodeDownTime)

    schedMap.get(nid).add(finishedItem)

    while (iterator.hasNext) {
      schedMap.get(nid).add(iterator.next())
    }
    ctx.environment.setNodeStatus(nid, NodeStatus.DOWN)
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

//  def onNodeFailed(ev: NodeFailed) = {
//    ctx.setTime(ev.eventTime)
//    val nid = ev.node.id
//    val schedMap = ctx.schedule.getMap
//    val nodeSched = schedMap.get(nid)
//    ctx.schedule.addNode(nid)
//    val iterator = nodeSched.iterator
//    var exit = false
//    var item: ScheduleItem = null
//    while (iterator.hasNext && !exit) {
//      item = iterator.next()
//      if (item.status == ScheduleItemStatus.RUNNING) {
//        exit = true
//      } else {
//        ctx.schedule.getMap.get(nid).add(item)
//      }
//    }
//
//    ctx.schedule.getMap.get(nid).add(
//      new TaskScheduleItem[T, N](id=item.id, name=item.name,
//        startTime = item.startTime,
//        endTime = ctx.currentTime,
//        status=ScheduleItemStatus.FAILED,
//        node=item.asInstanceOf[TaskScheduleItem[T, N]].node, task=item.asInstanceOf[TaskScheduleItem[T, N]].task))
//    while (iterator.hasNext) {
//      ctx.schedule.getMap.get(nid).add(iterator.next())
//    }
//    ctx.environment.setNodeStatus(nid, NodeStatus.DOWN)
//    queue.submitEvent(new Rescheduling(id="reshedule", name="rescheduling",
//      postTime = ctx.currentTime, eventTime=ctx.currentTime))
//    queue.submitEvent(new NodeUpped(id="node upped", name="node upped",
//      postTime = ctx.currentTime, eventTime = ctx.currentTime + nodeDownTime, node=ev.node))
//  }

//  def onNodeUpped(ev: NodeUpped) = {
//    ctx.setTime(ev.eventTime)
//    val nid = ev.node.id
//    ctx.environment.setNodeStatus(nid, NodeStatus.UP)
//    queue.submitEvent(new Rescheduling(id="reshedule", name="rescheduling",
//      postTime = ctx.currentTime, eventTime=ctx.currentTime))
//  }

  var failMap = Set[String]()
//  var failMap = Set[String]()

  private def taskFailer(taskScheduleItem: TaskScheduleItem[T, N]) = {
    //    taskScheduleItem.status = TaskScheduleItemStatus.RUNNING
    val dice = rnd.nextDouble()
    val tId = taskScheduleItem.task.id
    var fail = false
    if (failMap.contains(tId)) {
      fail = true
      failMap = failMap.filter(x => x != tId)
    }
    // TODO reliablity not via specific node
//    if (dice < taskScheduleItem.node.asInstanceOf[CapacityBasedNode].reliability) {
    if (!fail) {
      // Task will be finished
      val taskFinishedEvent = new TaskFinished(id = taskScheduleItem.id, name = taskScheduleItem.name, postTime = ctx.currentTime,
        eventTime = taskScheduleItem.endTime, taskScheduleItem.task, taskScheduleItem.node)
      queue.submitEvent(taskFinishedEvent)
    } else {
      // Task will be failed (random time between start and end of the current schedule item)
      val itemStart = taskScheduleItem.startTime
      val itemEnd = taskScheduleItem.endTime
//      val failTime = rnd.nextDouble() * (itemEnd - itemStart) + itemStart
      val failTime = itemStart
//      val taskFailedEvent = new NodeFailed(id = s"Node ${taskScheduleItem.node.id} failed", name = s"Node ${taskScheduleItem.node.id} failed", postTime = ctx.currentTime,
//        eventTime = failTime, taskScheduleItem.node)
//      queue.submitEvent(taskFailedEvent)
      val taskFailedEvent = new TaskFailed(id = taskScheduleItem.id, name = taskScheduleItem.name, postTime = ctx.currentTime,
        eventTime = failTime, taskScheduleItem.task, taskScheduleItem.node)
      queue.submitEvent(taskFailedEvent)
    }
  }
}


