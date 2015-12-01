package itmo.escience.simenv.algorithms

import java.util

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.SingleAppWorkload
import scala.collection.JavaConversions._

/**
 * Created by Nikolay on 12/1/2015.
 */
object HEFTScheduler extends Scheduler[DaxTask, CapacityBasedNode]{
  override def schedule(context: Context[DaxTask, CapacityBasedNode]): Schedule = {

    val wf = context.workload.asInstanceOf[SingleAppWorkload].app
    val newSchedule = Schedule.emptySchedule()
    val nodes = context.environment.nodes.filter(x => x.status == Node.UP)

    val tasks = prioritize(wf, nodes, context)

    for (task <- tasks) {
      val item = nodes.map((node) => newSchedule.findTimeSlot(task, node, context)).minBy(x => x.endTime)
      newSchedule.placeTask(item)
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
  private def prioritize(wf:Workflow, nodes:Seq[CapacityBasedNode], context:Context[DaxTask, CapacityBasedNode]):Seq[DaxTask] = {

    // prioritization
    // start with the end tasks
    val endTasks = wf.tasks.filter(task => task.children.isEmpty).map(x => x.asInstanceOf[DaxTask])
    // construct all nodes couples
    val nodeCouples: List[(CapacityBasedNode, CapacityBasedNode)] =
      nodes.foldLeft(List[(CapacityBasedNode, CapacityBasedNode)]()) ((acc, node) =>
        acc ++ nodes.dropWhile(x => x != node).drop(1).map(x => (node,x)))

    val rankMap = new util.HashMap[TaskId, Double]()

    val isReady = (task:DaxTask) => !task.isInstanceOf[HeadDaxTask] && task.children.forall(c => rankMap.containsKey(c.id))

    var tasksToCalcluateRank = endTasks
    while (tasksToCalcluateRank.nonEmpty) {

      for (task <- tasksToCalcluateRank) {
        val compCost = nodes.map(node => context.estimator.calcTime(task, node)).sum / nodes.length
        // TODO: add exceptional situation handling
        val communicationsCosts = task.children.map(p => {
          rankMap.get(p.id) + nodeCouples.map({ case (from, to) =>
            context.estimator.calcTransferTime((task,from), (p,to))}).sum / nodeCouples.length
        })
        val commCost = if (communicationsCosts.isEmpty) 0.0 else communicationsCosts.max

        rankMap.put(task.id, compCost + commCost)
      }

      // children have to be verified on 'ready-to-run'
      tasksToCalcluateRank = tasksToCalcluateRank.
        foldLeft(List[DaxTask]())((acc, x) => acc ++ x.parents.filter(x => isReady(x))).toSet.toList
    }

    rankMap.toList.sortBy({case (taskId, rank) => -rank}).map({case (taskId, rank) =>
      wf.taskById(taskId).asInstanceOf[DaxTask]}).toSeq
  }
}
