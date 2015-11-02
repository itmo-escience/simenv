package itmo.escience.Executors

import itmo.escience.Algorithms.Scheduler
import itmo.escience.Environment.Context
import itmo.escience.Environment.Entities.Schedule
import itmo.escience.Executors.Events._
import sun.reflect.generics.reflectiveObjects.NotImplementedException

/**
 * Created by Mishanya on 14.10.2015.
 */
trait Executor {
  
  def runSimulation():Unit

  def dispatchEvent(event:Event):Unit
}

/**
 * Perform discrete event-drivent simulation of workflows execution
 * @param scheduler algorithm for scheduling, must implement Scheduler interface
 * @param ctx contains description of computational environments and may perform actions on it
 */
class BaseExecutor(val scheduler:Scheduler, var ctx:Context) extends Executor {

  private val queue = new EventQueue()

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
  override def dispatchEvent(event: Event): Unit = {
      case InitEvent => onInitEvent()
      case ev: TaskStarted => onTaskStarted(ev)
      case ev: TaskFinished => onTaskFinished(ev)
      case ev: _ => throw new Exception(s"Unknown type of the event: ${ev.getClass}")
  }

  private def onInitEvent() = {
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
