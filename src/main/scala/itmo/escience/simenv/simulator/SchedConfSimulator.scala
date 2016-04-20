package itmo.escience.simenv.simulator

import itmo.escience.simenv.algorithms.Scheduler
import itmo.escience.simenv.algorithms.ga.CGAScheduler

//import itmo.escience.simenv.algorithms.gaOld.cga.CoevGAScheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.{CarrierNodeEnvironment, BasicContext}
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.simulator.events.{InitEvent, TaskStarted, _}
import itmo.escience.simenv.utilities.{ScheduleVisualizer, SimLogger}
import org.apache.logging.log4j.{LogManager, Logger}

import scala.util.Random


/**
  * Perform discrete event-drivent simulation of workflows execution
  *
  * @param scheduler algorithm for scheduling, must implement Scheduler interface
  * @param ctx       contains description of computational environments and may perform actions on it
  */
class SchedConfSimulator[T <: Task, N <: Node](scheduler: Scheduler, ctx: Context[T, N],
                                               nodeDownTime: Double,
                                               resDownTime: Double,
                                               val nodeResizeTime: Double)
  extends BasicSimulator[T, N](scheduler, ctx, nodeDownTime, resDownTime) {

  override val queue = new EventQueue()
  override val rnd = new Random()

  override val vis = new ScheduleVisualizer[T, N]

  override def runSimulation(): Unit = {
    super.runSimulation()
    SimLogger.logEnv(ctx.environment.asInstanceOf[Environment[CapacityBasedNode]])

    vis.drawSched(ctx.schedule, ctx.environment.asInstanceOf[CarrierNodeEnvironment[CapacityBasedNode]])
  }

  override def onInitEvent() = {

    SimLogger.log("Init event")
    val (schedule, env) = scheduler.asInstanceOf[CGAScheduler].coevSchedule[T, N](ctx, ctx.environment)

    SimLogger.log("Init schedule is generated")
    ctx.setEnvironment(env)
    ctx.applySchedule(schedule, queue)

    vis.drawSched(ctx.schedule, ctx.environment.asInstanceOf[CarrierNodeEnvironment[CapacityBasedNode]])

    SimLogger.logSched(schedule.asInstanceOf[Schedule[DaxTask, CapacityBasedNode]])
    SimLogger.logEnv(ctx.environment.asInstanceOf[Environment[CapacityBasedNode]])
    SimLogger.log("Init schedule has been applied")

    init_helper()
    SimLogger.log("Initial task has been handled")
  }

  override def onRescheduling(event: Rescheduling) = {
    println("reschedule")
    ctx.setTime(event.eventTime)
    // Reschedule
    if (ctx.environment.nodes.count(
      x => x.status == NodeStatus.UP && x.asInstanceOf[CapacityBasedNode].capacity > 0) != 0) {

      if (ctx.schedule.restTasks().nonEmpty) {

        var availableNodes = List[N]()
        var nodes = ctx.environment.nodes.filter(x => x.status == NodeStatus.UP)
        for (n <- nodes) {
          if (ctx.schedule.getMap.containsKey(n.id)) {
            val nodeSched = ctx.schedule.getMap.get(n.id)
            if (nodeSched.nonEmpty) {
              if (nodeSched.last.endTime <= ctx.currentTime) {
                availableNodes :+= n
              }
            } else {
              availableNodes :+= n
            }
          } else {
            availableNodes :+= n
          }
        }
        var sc: Schedule[T, N] = null
        if (availableNodes.size > 1) {
          println("coevo")
          val (sc1, env) = scheduler.asInstanceOf[CGAScheduler].coevSchedule(ctx, ctx.environment)
          sc = sc1
          queue.eq = queue.eq.filter(x => !x.isInstanceOf[TaskStarted])
          // Apply new schedule
          ctx.setEnvironment(env)
          ctx.applySchedule(sc, queue)
        } else {
          println("ga")
          sc = scheduler.schedule[T, N](ctx, ctx.environment)
          queue.eq = queue.eq.filter(x => !x.isInstanceOf[TaskStarted])
          // Apply new schedule
          ctx.applySchedule(sc, queue)
        }
        vis.drawSched(ctx.schedule, ctx.environment.asInstanceOf[CarrierNodeEnvironment[CapacityBasedNode]])


        SimLogger.log("Rescheduling has been completed")
        SimLogger.logSched(sc.asInstanceOf[Schedule[DaxTask, CapacityBasedNode]])

        // submit new events, if it is required after the failed task
        task_failed_after()
      }
    } else {
      queue.submitEvent(new Rescheduling(id = "reschedule", name = "reschedule", postTime = ctx.currentTime, eventTime = ctx.currentTime + nodeDownTime))
    }
  }

}


