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
    var resultSchedule: Schedule = new Schedule()
    resultSchedule.map = ctx.schedule.map
    val nodes: List[Node] = ctx.nodes

    var t: Task = null
    for (t <- tasks) {
      val n: Node = nodes(ctx.rnd.nextInt(nodes.size))
      val startTime: Double = resultSchedule.getNodeFreeTime(n, ctx)
      val runTime: Double = SimpleEstimator.estimateRunTime(n, t)
      val transferTime: Double = SimpleEstimator.estimateTransferTime(nodes, n, t)
      val endTime: Double = startTime + runTime + transferTime
      // Add new schedule item
      resultSchedule.map = resultSchedule.map.updated(n, resultSchedule.map(n) :+ new ScheduleItem(n, t, startTime, endTime, transferTime))
    }
    return resultSchedule
  }
}
