package itmo.escience.simenv.algorithms

import java.util

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.SingleAppWorkload
import itmo.escience.simenv.environment.modelling.Environment
import scala.collection.JavaConversions._

/**
 * Created by Nikolay on 12/1/2015.
 */
object HEFTScheduler extends Scheduler{
  override def schedule[T <: Task, N <: Node](context: Context[T, N], environment: Environment[N]): Schedule[T, N] = {

    if (!context.workload.isInstanceOf[SingleAppWorkload]) {
      throw new UnsupportedOperationException(s"Invalid workload type ${context.workload.getClass}. " +
        s"Currently only SingleAppWorkload is supported")
    }

    val wf = context.workload.asInstanceOf[SingleAppWorkload].app
    val newSchedule = context.schedule.fixedSchedule()
//    val nodes = context.environment.nodes.filter(x => x.status == Node.UP)
    val nodes = environment.nodes.filter(x => x.status == NodeStatus.UP)
    val fixed_tasks = newSchedule.scheduleItemsSeq().filter(x => x.status != ScheduleItemStatus.FAILED).map(x => x.asInstanceOf[TaskScheduleItem[T, N]].task.id)

    val tasks = prioritize[T, N](wf, nodes, context)


    // TODO make fixed_tasks as a function of schedule!!!

    for (task <- tasks.filter(x => !fixed_tasks.contains(x.id))) {
      val item = nodes.map((node) => newSchedule.findTimeSlot(task, node, context)).minBy(x => x.endTime)
      newSchedule.placeTask(task, item.node, context)
    }
    newSchedule
  }

  /**
   * The method prioritize tasks for HEFT algorithm
   * ATTENTION! task is assumed to have properly defined equals and hashcode
   * @param wf
   * @param nodes
   * @param context
   * @return
   */
  private def prioritize[T <: Task, N <: Node](wf:Workflow, nodes:Seq[N], context:Context[T, N]): Seq[T] = {

    // prioritization
    // start with the end tasks
    val endTasks = wf.tasks.filter(task => task.children.isEmpty).map(x => x.asInstanceOf[T]).distinct
    // construct all nodes couples
    val nodeCouples: List[(N, N)] =
      nodes.foldLeft(List[(N, N)]()) ((acc, node) =>
        acc ++ nodes.dropWhile(x => x != node).drop(1).map(x => (node,x)))

    val rankMap = new util.HashMap[TaskId, Double]()

    val isReady = (task:T) => !task.isInstanceOf[HeadDaxTask] && task.children.forall(c => rankMap.containsKey(c.id))

    var tasksToCalcluateRank = endTasks
    while (tasksToCalcluateRank.nonEmpty) {

      for (task <- tasksToCalcluateRank) {
        val compCost = nodes.map(node => context.estimator.calcTime(task, node)).sum / nodes.length
        // TODO: add exceptional situation handling
        var commCost = 0.0
        if (nodeCouples.nonEmpty) {
          val communicationsCosts = task.children.map(p => {
            rankMap.get(p.id) + nodeCouples.map({ case (from, to) =>
              context.estimator.calcTransferTime((task, from), (p.asInstanceOf[T], to))
            }).sum / nodeCouples.length
          })
          commCost = if (communicationsCosts.isEmpty) 0.0 else communicationsCosts.max
        } else {
          commCost = task.children.map(p => rankMap.get(p.id)).sum
        }
        rankMap.put(task.id, compCost + commCost)
      }

      // children have to be verified on 'ready-to-run'
      tasksToCalcluateRank = tasksToCalcluateRank.
        foldLeft(List[T]())((acc, x) => acc ++ x.parents.filter(x => isReady(x.asInstanceOf[T])).asInstanceOf[List[T]]).distinct
    }
    val result = rankMap.toList.sortBy({case (taskId, rank) => -rank}).map({case (taskId, rank) =>
      wf.taskById(taskId)}).toSeq.asInstanceOf[Seq[T]]
    result
  }
}
