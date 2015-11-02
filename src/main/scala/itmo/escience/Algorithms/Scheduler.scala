package itmo.escience.Algorithms

import itmo.escience.Environment.Context
import itmo.escience.Environment.Entities.{Node, Schedule, Task}

/**
 * Created by Mishanya on 14.10.2015.
 */
trait Scheduler {

  //TODO: method can schedule also datatransfer and actions dedicated to virtual machines and physical nodes
  /**
   * schedules workflows' tasks on nodes.
   * Also, performs rescheduling according to the current state of the environment
   * @param ctx description of current environment state
   * @return schedule for further execution
   */
  def schedule(ctx: Context): Schedule

  //TODO: clean it up after the description of the interface has been finished
//  // Prepare context to schedule. Remove all free tasks into new tasks list.
//  def reschedule(ctx: Context): List[Task] = {
//    // Result list of rescheduled tasks
//    var schedTasks: List[Task] = List()
//
//    var node: Node = null
//    for (node <- ctx.nodes) {
//      ctx.schedule.map(node).foreach(item => schedTasks ::= item.task)
//      ctx.schedule.map -= node
//      ctx.schedule.map += (node -> List())
//    }
//    return schedTasks
//  }
}
