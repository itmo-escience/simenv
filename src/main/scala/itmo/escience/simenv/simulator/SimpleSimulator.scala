package itmo.escience.simenv.simulator

import itmo.escience.simenv.algorithms.Scheduler
import itmo.escience.simenv.environment.Context
import itmo.escience.simenv.simulator.events.{InitEvent, TaskStarted, _}

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
    queue.submitEvent(InitEvent.instance)
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

    val schedule = scheduler.schedule(ctx)
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
}


