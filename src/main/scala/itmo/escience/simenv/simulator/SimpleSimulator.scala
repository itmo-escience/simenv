package itmo.escience.simenv.simulator

import itmo.escience.algorithms.Scheduler
import itmo.escience.environment.Context
import itmo.escience.environment.entities.Schedule
import itmo.escience.simenv.simulator.Events.{TaskStarted, InitEvent}
import itmo.escience.simulator.events._
import sun.reflect.generics.reflectiveObjects.NotImplementedException

/**
 * Perform discrete event-drivent simulation of workflows execution
 * @param scheduler algorithm for scheduling, must implement Scheduler interface
 * @param ctx contains description of computational environments and may perform actions on it
 */
class SimpleSimulator(val scheduler:Scheduler, var ctx:Context) extends Simulator {

  val queue = new EventQueue()

  /**
   * generates and adds the very first event [[InitEvent]] to the event queue
   * Scheduling for initial state of environment have to be placed in the handler of this event
   */
  def init() = {
    queue.addEvent(InitEvent.instance)
  }

  /**
   * starts the simulation.
   * The simulation will finish when there is not any event in the queue
   */
  override def runSimulation(): Unit = {
    while (!queue.isEmpty()) {
      val event = queue.next()
      dispatchEvent(event)
    }
  }

  /**
   * chooses an appropriate event handler for the current event
   * @param event
   */
   def dispatchEvent(event: Event): Unit = {
      case InitEvent => onInitEvent()
      case ev: TaskStarted => onTaskStarted(ev)
      case ev: TaskFinished => onTaskFinished(ev)
      case ev: _ => throw new Exception(s"Unknown type of the event: ${ev.getClass}")
  }

  private def onInitEvent() = {

    // for any event there exists a general sequence of steps
    // 1. update context according to the event
    // 2. update CURRENRT schedule according to the event
    // 3. check if the situation needs rescheduling: yes - 4, no - 6
    // 4. create new schedule (or run background operations and etc)
    // 5. apply new schedule to eventQueue (if any) and create new context
    // 6. Exit

    val schedule = scheduler.schedule(ctx=ctx)
    // build new context according to the new schedule
    ctx = changeEventsBySchedule(ctx, schedule)
    //TODO: add logging here
    throw new NotImplementedError()
  }

  private def onTaskStarted(event: TaskStarted) = {
    //TODO: add logging here
    throw new NotImplementedError()
  }

  private def onTaskFinished(event: TaskFinished) = {
    //TODO: add logging here
    throw new NotImplementedError()
  }

  /**
   * constructs new queue of events according to the new schedule
   * @param ctx current context which contains actual schedule (not-changed schedule before scheduling)
   * @param new_schedule new events will be generated according to
   * @return
   */
  private def changeEventsBySchedule(ctx:Context, new_schedule:Schedule): Context = {
    throw new NotImplementedError()
  }
}
