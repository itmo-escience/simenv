package itmo.escience.Algorithms

import itmo.escience.Environment.Context
import itmo.escience.Environment.Entities.{Node, Schedule, Task}

/**
 * Created by Mishanya on 14.10.2015.
 */
trait Scheduler {
  // Tasks scheduling
  def schedule(ctx: Context, tasks: List[Task]): Schedule

  // Prepare context to schedule. Remove all free tasks into new tasks list.
  def reschedule(ctx: Context): List[Task] = {
    // Result list of rescheduled tasks
    var schedTasks: List[Task] = List()

    var node: Node = null
    for (node <- ctx.nodes) {
      ctx.schedule.map(node).foreach(item => schedTasks ::= item.task)
      ctx.schedule.map -= node
      ctx.schedule.map += (node -> List())
    }
    return schedTasks
  }
}
