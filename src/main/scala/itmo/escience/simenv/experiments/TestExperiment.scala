package itmo.escience.simenv.experiments

import itmo.escience.simenv.environment.Context
import itmo.escience.simenv.environment.entities.Schedule
import itmo.escience.simenv.simulator.events.{Event, EventQueue}
import itmo.escience.simenv.utilities.ScheduleVisualizer

/**
 * Created by Mishanya on 14.10.2015.
 */
object TestExperiment extends Experiment {
  override def apply()= {
    throw new NotImplementedError()

//    println("It shall be done!")
//
//    // Initialize context
//    val ctx: Context = new Context()
//    // Set initial computing environment
//    ctx.addNodes(nodes)
//    // Initialize event queue
//    val eq: EventQueue = new EventQueue()
//    // Create initial schedule
//    val initSchedule: Schedule = schedAlg.schedule(ctx, tasks)
//    // Apply this initial schedule
//    ctx.applySchedule(initSchedule, eq)
//
//    if (!eq.isCorrectOrder()) {
//      println("Incorrect eq!!!")
//    }
//
//    // Visualizer
//    val scheduleVisualizer: ScheduleVisualizer = new ScheduleVisualizer()
//    val drawScheds: Boolean = true
//
//    // Handle events from event queue
//    while (!eq.isEmpty()) {
//      if (drawScheds) {
//        // Draw current schedule
//        scheduleVisualizer.drawSched(ctx.schedule)
//      }
//
//      // Take next event from event queue
//      val curEvent: Event = eq.next();
//      println("Event time = " + curEvent.startTime)
//
//      // Set context time
//      if (curEvent.startTime < ctx.time) {
//        throw new Exception("Event time can't be less then current time.")
//      }
//      ctx.time = curEvent.startTime
//
//      // Handle current event
//      EventHandler.handle(curEvent, ctx, eq, schedAlg)
//      if (!eq.isCorrectOrder()) {
//        println("Incorrect eq!!!")
//      }
    }

    //TODO what will be a result?
}
