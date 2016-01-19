package itmo.escience.simenv.simulator

import itmo.escience.simenv.algorithms.Scheduler
import itmo.escience.simenv.algorithms.ga.cga.CoevGAScheduler
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
class SchedConfSimulator[T <: Task, N <: Node](scheduler: Scheduler[T, N], ctx:Context[T, N]) extends BasicSimulator[T, N](scheduler, ctx) {

  override val queue = new EventQueue()
  override val rnd = new Random()

  override def runSimulation(): Unit = {
    super.runSimulation()
    SimLogger.logEnv(ctx.environment.asInstanceOf[Environment[CpuTimeNode]])
  }

  override def onInitEvent() = {

    SimLogger.log("Init event")
    val (schedule, env) = scheduler.asInstanceOf[CoevGAScheduler].scheduleAndConfiguration(ctx.asInstanceOf[Context[DaxTask, Node]], ctx.environment.asInstanceOf[Environment[Node]])

    SimLogger.log("Init schedule is generated")
    ctx.setEnvironment(env.asInstanceOf[Environment[N]])
    ctx.applySchedule(schedule, queue)

    SimLogger.logSched(schedule)
    SimLogger.logEnv(ctx.environment.asInstanceOf[Environment[CpuTimeNode]])
    SimLogger.log("Init schedule has been applied")

    init_helper()
    SimLogger.log("Initial task has been handled")
  }

  override def onTaskFailed(event: TaskFailed) = {
    task_failed_before(event)

    // Reschedule
    val (sc, env) = scheduler.asInstanceOf[CoevGAScheduler].scheduleAndConfiguration(ctx.asInstanceOf[Context[DaxTask, Node]], ctx.environment.asInstanceOf[Environment[Node]])
    ctx.setEnvironment(env.asInstanceOf[Environment[N]])
    queue.eq = queue.eq.filter(x => !x.isInstanceOf[TaskStarted])
    ctx.applySchedule(sc, queue)
    SimLogger.log("Rescheduling has been completed")
    SimLogger.logSched(sc)
    SimLogger.logEnv(ctx.environment.asInstanceOf[Environment[CpuTimeNode]])

    task_failed_after()
  }
}


