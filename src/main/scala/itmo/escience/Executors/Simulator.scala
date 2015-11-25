package itmo.escience.Executors

import com.sun.applet2.preloader.event.InitEvent
import itmo.escience.Algorithms.Scheduler
import itmo.escience.Environment.{Environment, Workload, Context}
import itmo.escience.Environment.Entities.{Node, Schedule, Task}
import itmo.escience.Executors.Events.{InitEvent, Event, EventHandler, EventQueue}
import itmo.escience.Utilities.ScheduleVisualizer

/**
 * Created by Mishanya on 14.10.2015.
 */
object Simulator {
  def simulate(workload: Workload, environment: Environment, schedAlg: Scheduler): Unit = {
    println("It shall be done!")

    // Initialize context
    val ctx: Context = new Context()
    ctx.eventQueue.addEvent(InitEvent.instance)
    ctx.setEnvironment(environment)
    ctx.setWorkload(workload)
    // Create initial schedule
    val initSchedule: Schedule = schedAlg.schedule(ctx, workload.tasks)
    // Apply this initial schedule
    ctx.applySchedule(initSchedule)

    if (!ctx.eventQueue.isCorrectOrder()) {
      println("Incorrect eq!!!")
    }

    // Visualizer
    val scheduleVisualizer: ScheduleVisualizer = new ScheduleVisualizer()
    val drawScheds: Boolean = false

    // Handle events from event queue
    while (!ctx.eventQueue.isEmpty()) {
      if (drawScheds) {
        // Draw current schedule
        scheduleVisualizer.drawSched(ctx.schedule)
      }

      // Take next event from event queue
      val curEvent: Event = ctx.eventQueue.next();
      println("Event time = " + curEvent.startTime)

      // Set context time
      if (curEvent.startTime < ctx.time) {
        throw new Exception("Event time can't be less then current time.")
      }
      ctx.time = curEvent.startTime

      // Handle current event
      EventHandler.dispatchEvent(curEvent, ctx, schedAlg)
      if (!ctx.eventQueue.isCorrectOrder()) {
        println("Incorrect eq!!!")
      }
    }

    //TODO what will be a result?
  }
}
