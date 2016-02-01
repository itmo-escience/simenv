package itmo.escience.simenv.simulator

import itmo.escience.simenv.algorithms.Scheduler
import itmo.escience.simenv.algorithms.ga.CGAScheduler

//import itmo.escience.simenv.algorithms.gaOld.cga.CoevGAScheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.{CarrierNodeEnvironment, BasicContext}
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.simulator.events.{InitEvent, TaskStarted, _}
import itmo.escience.simenv.utilities.SimLogger
import org.apache.logging.log4j.{LogManager, Logger}

import scala.util.Random


/**
 * Perform discrete event-drivent simulation of workflows execution
 * @param scheduler algorithm for scheduling, must implement Scheduler interface
 * @param ctx contains description of computational environments and may perform actions on it
 */
class SchedConfSimulator[T <: Task, N <: Node](scheduler: Scheduler, ctx:Context[T, N],
                                               nodeDownTime: Double,
                                               resDownTime: Double,
                                               val nodeResizeTime: Double)
  extends BasicSimulator[T, N](scheduler, ctx, nodeDownTime, resDownTime) {

  override val queue = new EventQueue()
  override val rnd = new Random()

  override def runSimulation(): Unit = {
    super.runSimulation()
    SimLogger.logEnv(ctx.environment.asInstanceOf[Environment[CapacityBasedNode]])
  }

  override def onInitEvent() = {

    SimLogger.log("Init event")
    val (schedule, env) = scheduler.asInstanceOf[CGAScheduler].coevSchedule[T, N](ctx, ctx.environment)

    SimLogger.log("Init schedule is generated")
    ctx.setEnvironment(env)
    ctx.applySchedule(schedule, queue)

    SimLogger.logSched(schedule.asInstanceOf[Schedule[DaxTask, CapacityBasedNode]])
    SimLogger.logEnv(ctx.environment.asInstanceOf[Environment[CapacityBasedNode]])
    SimLogger.log("Init schedule has been applied")

    init_helper()
    SimLogger.log("Initial task has been handled")
  }

  override def onRescheduling(event: Rescheduling) = {
    ctx.setTime(event.eventTime)
    // Reschedule
    if (ctx.environment.nodes.count(
      x => x.status == NodeStatus.UP && x.asInstanceOf[CapacityBasedNode].capacity > 0) != 0) {

      if (ctx.schedule.restTasks().nonEmpty) {

        val (sc, env) = scheduler.asInstanceOf[CGAScheduler].coevSchedule(ctx, ctx.environment)
        queue.eq = queue.eq.filter(x => !x.isInstanceOf[TaskStarted])
        // Apply new schedule
        ctx.setEnvironment(env)
        ctx.applySchedule(sc, queue)
        SimLogger.log("Rescheduling has been completed")
        SimLogger.logSched(sc.asInstanceOf[Schedule[DaxTask, CapacityBasedNode]])

        // submit new events, if it is required after the failed task
        task_failed_after()
      }
    } else {
      queue.submitEvent(new Rescheduling(id="reschedule", name="reschedule", postTime = ctx.currentTime, eventTime = ctx.currentTime + nodeDownTime) )
    }
  }

//  override def onTaskFailed(event: TaskFailed) = {
//    task_failed_before(event)
//
//    // Reschedule
//    val (sc, env) = scheduler.asInstanceOf[CGAScheduler].coevSchedule[T, N](ctx, ctx.environment)
//    ctx.setEnvironment(env)
//    queue.eq = queue.eq.filter(x => !x.isInstanceOf[TaskStarted])
//    ctx.applySchedule(sc, queue)
//    SimLogger.log("Rescheduling has been completed")
//    SimLogger.logSched(sc.asInstanceOf[Schedule[DaxTask, CapacityBasedNode]])
//    SimLogger.logEnv(ctx.environment.asInstanceOf[Environment[CapacityBasedNode]])
//
//    task_failed_after()
//  }
}


