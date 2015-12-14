package itmo.escience.simenv.simulator


import itmo.escience.simenv.algorithms.Scheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.simulator.events.{InitEvent, TaskStarted, _}
import itmo.escience.simenv.utilities.Utilities
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
      val event = queue.next()
      dispatchEvent(event)
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
      //TODO remove "asInstanceOf"
      val firstItem = schedMap.get(n.id).head.asInstanceOf[TaskScheduleItem]
      val newFirstItem = firstItem.changeStatus(TaskScheduleItemStatus.RUNNING)
      taskFailer(firstItem)
      // TODO check this mutation
      schedMap.put(n.id, schedMap.get(n.id).tail + newFirstItem)
    }
    logger.trace("Initial task has been handled")
  }

  private def onTaskStarted(event: TaskStarted) = {
    //TODO: add logging here
    throw new NotImplementedError()
  }

  private def onTaskFinished(event: TaskFinished) = {
    //TODO: add logging here
    throw new NotImplementedError()
  }

  private def taskFailer(taskScheduleItem: TaskScheduleItem) = {
    //TODO: add logging here
//    taskScheduleItem.status = TaskScheduleItemStatus.RUNNING
    val dice = rnd.nextDouble()
    if (dice < taskScheduleItem.node.reliability) {
      // Task will be finished
      val taskFinishedEvent = new TaskFinished(id=Utilities.generateId(), name=taskScheduleItem.name, postTime=ctx.currentTime,
        eventTime = taskScheduleItem.endTime, taskScheduleItem.task, taskScheduleItem.node)
      queue.submitEvent(taskFinishedEvent)
    } else {
      // Task will be failed (random time between start and end of the current schedule item)
      val itemStart = taskScheduleItem.startTime
      val itemEnd = taskScheduleItem.endTime
      val failTime = rnd.nextDouble() * (itemEnd - itemStart) + itemStart
      val taskFailedEvent = new TaskFinished(id=Utilities.generateId(), name=taskScheduleItem.name, postTime=ctx.currentTime,
        eventTime = failTime, taskScheduleItem.task, taskScheduleItem.node)
      queue.submitEvent(taskFailedEvent)
    }
  }
}


