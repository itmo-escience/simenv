//package itmo.escience.simenv.simulator
//
//import itmo.escience.simenv.algorithms.Scheduler
//import itmo.escience.simenv.algorithms.ga.cga.CoevGAScheduler
//import itmo.escience.simenv.environment.entities._
//import itmo.escience.simenv.environment.entitiesimpl.{BasicContext, CoreRamEnvironment}
//import itmo.escience.simenv.simulator.events.{InitEvent, TaskStarted, _}
//import org.apache.logging.log4j.{LogManager, Logger}
//
//import scala.util.Random
//
//
///**
// * Perform discrete event-drivent simulation of workflows execution
// * @param scheduler algorithm for scheduling, must implement Scheduler interface
// * @param ctx contains description of computational environments and may perform actions on it
// */
//class HEFTSimulator(val scheduler: Scheduler[DaxTask, CoreRamNode], var ctx:Context[DaxTask, CoreRamNode]) extends Simulator {
//
//  val logger: Logger = LogManager.getLogger("logger")
//  val queue = new EventQueue()
//  val rnd = new Random()
//
//  /**
//   * generates and adds the very first event [[InitEvent]] to the event queue
//   * Scheduling for initial state of environment have to be placed in the handler of this event
//   */
//  def init() = {
//    queue.submitEvent(InitEvent.instance)
//    logger.trace("Init event submitted")
//  }
//
//  /**
//   * starts the simulation.
//   * The simulation will finish when there is not any event in the queue
//   */
//  override def runSimulation(): Unit = {
//
//    while (!queue.isEmpty) {
////      println(queue.print())
//      val event = queue.next()
//
//      dispatchEvent(event)
////      println("---next event---")
////      print(s"current time ${ctx.currentTime}")
////      println(s"event time ${event.eventTime}")
//    }
//
//    println("finish schedule")
//    println(ctx.schedule.prettyPrint())
//    println(ctx.environment.asInstanceOf[CoreRamEnvironment].vms.map(x => s"${x.cores}; ${x.ram}"))
//    println(s"makespan: ${ctx.schedule.makespan()}")
//  }
//
//  /**
//   * chooses an appropriate event handler for the current event
//   * @param event
//   */
//  def dispatchEvent(event: Event): Unit = event match {
//    case InitEvent => onInitEvent()
//    case ev: TaskStarted => onTaskStarted(ev)
//    case ev: TaskFinished => onTaskFinished(ev)
//    case ev: TaskFailed => onTaskFailed(ev)
//    case _ => throw new Exception(s"Unknown type of the event: ${event.getClass}")
//  }
//
//  private def onInitEvent() = {
//
//    // for any event there exists a general sequence of steps
//    // 1. update context according to the event
//    // 2. update CURRENRT schedule according to the event
//    // 3. check if the situation needs rescheduling: yes - 4, no - 6
//    // 4. create new schedule (or run background operations and etc)
//    // 5. apply new schedule to eventQueue (if any) and create new context
//    // 6. Exit
//
//    //val schedule = scheduler.schedule(ctx)
//    //TODO: add logging here
//    logger.trace("Init event")
//    val schedule = scheduler.schedule(ctx, ctx.environment)
////    ctx.asInstanceOf[BasicContext[DaxTask, CoreRamHddBasedNode]].setEnvironment(env)
//    logger.trace("Init schedule is generated")
//    // This function applies new schedule and generates events
//    ctx.applySchedule(schedule, queue)
//
//    println("Initial schedule:")
//    println(ctx.schedule.prettyPrint())
//    println(ctx.environment.asInstanceOf[CoreRamEnvironment].vms.map(x => s"${x.cores}; ${x.ram}"))
//
//    logger.trace("Init schedule has been applied")
//    // Generate initial events
//    val schedMap = ctx.schedule.getMap()
//    for (n <- ctx.environment.asInstanceOf[CoreRamEnvironment].vms) {
//      //TODO try to remove "asInstanceOf"
//      val firstItem = schedMap.get(n.id).head.asInstanceOf[TaskScheduleItem]
//      queue.submitEvent(new TaskStarted(id=firstItem.id, name=firstItem.name,
//        postTime=ctx.currentTime, eventTime=firstItem.startTime,
//        task=firstItem.task, node=firstItem.node))
//    }
//    logger.trace("Initial task has been handled")
//  }
//
//  private def onTaskStarted(event: TaskStarted) = {
//    //TODO: add logging here
//    logger.trace(s"Task has been started ${event.task.name}")
//    ctx.setTime(event.eventTime)
//    val nid = event.node.id
//    val schedMap = ctx.schedule.getMap()
//    val nodeSched = schedMap.get(nid)
//    val (iterator, curItem, counter) = ctx.schedule.findItemInNodeSched(nid, event.id)
//    schedMap.put(nid, nodeSched.take(counter))
//    schedMap.get(nid).add(curItem.asInstanceOf[TaskScheduleItem].changeStatus(ScheduleItemStatus.RUNNING))
//    taskFailer(curItem.asInstanceOf[TaskScheduleItem])
//    while (iterator.hasNext) {
//      schedMap.get(nid).add(iterator.next())
//    }
//  }
//
//  private def onTaskFinished(event: TaskFinished) = {
//    ctx.setTime(event.eventTime)
//    //TODO: add logging here
//    // 1) set status on finished
//    val nid = event.node.id
//    val schedMap = ctx.schedule.getMap()
//    val nodeSched = schedMap.get(nid)
//    val (iterator, curItem, counter) = ctx.schedule.findItemInNodeSched(nid, event.id)
//
//    schedMap.put(nid, nodeSched.take(counter))
//    val finishedItem = curItem.asInstanceOf[TaskScheduleItem].changeStatus(ScheduleItemStatus.FINISHED)
//    schedMap.get(nid).add(finishedItem)
//
//    if (iterator.hasNext) {
//      val nextItem = iterator.next().asInstanceOf[TaskScheduleItem]
//      queue.submitEvent(new TaskStarted(id=nextItem.id, name=nextItem.name,
//        postTime=ctx.currentTime, eventTime=nextItem.startTime,
//        task=nextItem.task, node=nextItem.node))
//      schedMap.get(nid).add(nextItem)
//    }
//    while (iterator.hasNext) {
//      schedMap.get(nid).add(iterator.next())
//    }
//  }
//
//  private def onTaskFailed(event: TaskFailed) = {
//    ctx.setTime(event.eventTime)
//    //TODO: add logging here
//
//    // Mark this item as failed
//    val nid = event.node.id
//    val schedMap = ctx.schedule.getMap()
//    val nodeSched = schedMap.get(nid)
//    val (iterator, curItem, counter) = ctx.schedule.findItemInNodeSched(nid, event.id)
//
//    schedMap.put(nid, nodeSched.take(counter))
//    val finishedItem = curItem.asInstanceOf[TaskScheduleItem].setToFailed(event.eventTime)
//    schedMap.get(nid).add(finishedItem)
//
//    while (iterator.hasNext) {
//      schedMap.get(nid).add(iterator.next())
//    }
//
//    // Reschedule
//    val sc = scheduler.schedule(ctx, ctx.environment)
//
////    println(s"Rescheduled schedule:\n ${sc.prettyPrint()}")
//    queue.eq = queue.eq.filter(x => !x.isInstanceOf[TaskStarted])
//    // Apply new schedule
//    ctx.applySchedule(sc, queue)
//
//    // submit new events, if it is required after the failed task
//
//    val newFixedSchedule = ctx.schedule.fixedSchedule()
//    for (nid <- newFixedSchedule.nodeIds()) {
//      val fixNodeSched = newFixedSchedule.getMap().get(nid)
//      if (fixNodeSched.nonEmpty && fixNodeSched.last.status != ScheduleItemStatus.RUNNING) {
//        val lastItem = fixNodeSched.last
//        val (newIterator, newCurItem, newCounter) = ctx.schedule.findItemInNodeSched(nid, lastItem.id)
//        if (newIterator.hasNext) {
//          val nextItem = newIterator.next().asInstanceOf[TaskScheduleItem]
//          if (!queue.eq.map(x => x.id).toList.contains(nextItem.id)) {
//            queue.submitEvent(new TaskStarted(id=nextItem.id, name=nextItem.name,
//              postTime=ctx.currentTime, eventTime=nextItem.startTime,
//              task=nextItem.task, node=nextItem.node))
//          }
//        }
//      } else {
//        val newNodeSched = ctx.schedule.getMap().get(nid)
//        if (newNodeSched.nonEmpty) {
//          val firstItem = newNodeSched.head.asInstanceOf[TaskScheduleItem]
//          if (firstItem.status == ScheduleItemStatus.UNSTARTED && !queue.eq.map(x => x.id).toList.contains(firstItem.id)) {
//            queue.submitEvent(new TaskStarted(id = firstItem.id, name = firstItem.name,
//              postTime = ctx.currentTime, eventTime = firstItem.startTime,
//              task = firstItem.task, node = firstItem.node))
//          }
//        }
//      }
//    }
//  }
//
//  private def taskFailer(taskScheduleItem: TaskScheduleItem) = {
//    //TODO: add logging here
////    taskScheduleItem.status = TaskScheduleItemStatus.RUNNING
//    val dice = rnd.nextDouble()
//    if (dice < taskScheduleItem.node.reliability) {
//      // Task will be finished
//      val taskFinishedEvent = new TaskFinished(id=taskScheduleItem.id, name=taskScheduleItem.name, postTime=ctx.currentTime,
//        eventTime = taskScheduleItem.endTime, taskScheduleItem.task, taskScheduleItem.node)
//      queue.submitEvent(taskFinishedEvent)
//    } else {
//      // Task will be failed (random time between start and end of the current schedule item)
//      val itemStart = taskScheduleItem.startTime
//      val itemEnd = taskScheduleItem.endTime
//      val failTime = rnd.nextDouble() * (itemEnd - itemStart) + itemStart
//      val taskFailedEvent = new TaskFailed(id=taskScheduleItem.id, name=taskScheduleItem.name, postTime=ctx.currentTime,
//        eventTime = failTime, taskScheduleItem.task, taskScheduleItem.node)
//      queue.submitEvent(taskFailedEvent)
//    }
//  }
//}
//
//
