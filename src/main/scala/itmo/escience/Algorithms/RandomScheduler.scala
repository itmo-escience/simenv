package itmo.escience.Algorithms

import itmo.escience.Environment.Context
import itmo.escience.Environment.Entities.{ScheduleItem, Node, Schedule, Task}

/**
 * Created by Mishanya on 14.10.2015.
 */
class RandomScheduler extends Scheduler{
  // Tasks scheduling
  def schedule(ctx: Context, tasks: List[Task]): Schedule = {
    // Copy current schedule
    //TODO check, does it makes change in current schedule, without copying
    var resultSchedule: Schedule = ctx.schedule
    val nodes: List[Node] = ctx.nodes

    var t: Task = null
    for (t <- tasks) {
      val n: Node = nodes(ctx.rnd.nextInt(nodes.size))
      //TODO add data transfer time to endTime!!!
      //TODO add function, which will estimate execute time into Scheduler trait, or new class Estimator!!!
      val startTime: Double = resultSchedule.getNodeFreeTime(n, ctx)
      val endTime: Double = startTime + t.execTime / n.capacity
      // Add new schedule item
      resultSchedule.map(n) ::= new ScheduleItem(n, t, startTime, endTime)
    }
    return resultSchedule
  }
}
