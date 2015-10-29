package itmo.escience

import itmo.escience.Algorithms.Scheduler
import itmo.escience.Environment.Context
import itmo.escience.Environment.Entities.{Schedule, Node, Task}
import itmo.escience.Environment.Events.{EventQueue, EventHandler, Event}
import itmo.escience.Utilities.ScheduleVisualizer

/**
 * Created by Mishanya on 14.10.2015.
 */
object Simulator {
  def simulate(tasks: List[Task], nodes: List[Node], schedAlg: Scheduler): Unit = {
    println("It shall be done!")

    // Initialize context
    val ctx: Context = new Context()
    // Set initial computing environment
    ctx.addNodes(nodes)
    // Initialize event queue
    val eq: EventQueue = new EventQueue()
    // Create initial schedule
    val initSchedule: Schedule = schedAlg.schedule(ctx, tasks)
    // Apply this initial schedule
    ctx.applySchedule(initSchedule, eq)

    println("Correct eq = " + eq.isCorrectOrder())

    // Visualizer
    val scheduleVisualizer: ScheduleVisualizer = new ScheduleVisualizer()
    val drawScheds: Boolean = true

    // Handle events from event queue
    while (!eq.isEmpty()) {
      if (drawScheds) {
        // Draw current schedule
        scheduleVisualizer.drawSched(ctx.schedule)
      }

      // Take next event from event queue
      val curEvent: Event = eq.next();
      println("Event time = " + curEvent.startTime)

      // Set context time
      if (curEvent.startTime < ctx.time) {
        throw new Exception("Event time can't be less then current time.")
      }
      ctx.time = curEvent.startTime

      // Handle current event
      EventHandler.handle(curEvent, ctx, eq, schedAlg)
      println("Correct eq = " + eq.isCorrectOrder())
    }

    if (drawScheds) {
      // Draw last schedule
      scheduleVisualizer.drawSched(ctx.schedule)
    }

    //TODO what will be a result?
  }
}
