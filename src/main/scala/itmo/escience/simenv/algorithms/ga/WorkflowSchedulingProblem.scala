package itmo.escience.simenv.algorithms.ga

import java.util

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.{BasicEstimator, BasicEnvironment}
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.utilities.MathFunctions

import scala.collection.JavaConversions._

/**
 * Created by user on 02.12.2015.
 */

object WorkflowSchedulingProblem {

  def scheduleToSolution[T <: Task, N <: Node](schedule:Schedule[T, N], context: Context[T, N], environment: Environment[N]):WFSchedSolution = {
    val taskItems = schedule.scheduleItemsSeq().filter({
      case x: TaskScheduleItem[T, N] => true
      case _ => false
    }).map(x => x.asInstanceOf[TaskScheduleItem[T, N]])
    val fixed = context.schedule.fixedSchedule()
    var fixed_tasks = List[String]()
    for (n <- fixed.nodeIds()) {
      fixed_tasks = fixed_tasks ++ fixed.getMap.get(n).toList.filter(x => x.status != ScheduleItemStatus.FAILED
      ).map(x => x.asInstanceOf[TaskScheduleItem[T, N]].task.id)
    }
    val restTasks = taskItems.filter(x => !fixed_tasks.contains(x.task.id))
    val genes = restTasks.map(x => MappedTask(x.task.id,
      environment.asInstanceOf[BasicEnvironment].indexOfNode(x.node.id),
      MathFunctions.getZPercents(x.task.asInstanceOf[DaxTask], x.endTime-x.startTime))).toList
    new WFSchedSolution(genes)
  }

  def solutionToSchedule[T <: Task, N <: Node](solution: WFSchedSolution, context: Context[T, N], environment: Environment[N]): Schedule[T, N] = {
    val newSchedule = context.schedule.fixedSchedule()

    // repair sequence in relation with parent-child dependencies
    // construct new schedule by placing it in task-by-task manner
    val repairedOrdering = repairOrdering(solution, context, environment)

    for (x <- repairedOrdering) {
      val (task, nodeId) = x
      newSchedule.placeTask(task,
        environment.nodeById(nodeId),
        context)
    }
    newSchedule.asInstanceOf[Schedule[T, N]]
  }

  def repairOrdering[T <: Task, N <: Node](solution: WFSchedSolution, context: Context[T, N], environment: Environment[N]): List[(T, NodeId)] = {
    val wf = context.workload.apps.head
    val tasksSeq = new util.TreeSet[Pair[(T, NodeId)]](solution.genSeq.zipWithIndex
      .map( { case (x, i) =>
        new Pair(i,
          (wf.taskById(x.taskId), environment.nodes(x.nodeIdx).id)
        )}
      ))

    val mappedTasks = new util.HashMap[TaskId, (T, NodeId)]()
    // add all task from fixed schedule
    val fixedSched = context.schedule.fixedSchedule()
    for (nid <- fixedSched.nodeIds()) {
      for (item <- fixedSched.getMap.get(nid)) {
        val taskScItem = item.asInstanceOf[TaskScheduleItem[T, N]]
        if (taskScItem.status != ScheduleItemStatus.FAILED) {
          mappedTasks.put(taskScItem.task.id, (taskScItem.task, taskScItem.node.id))
        }
      }
    }

    val repairedOrdering: java.util.ArrayList[(T, NodeId)] = new util.ArrayList[(T, NodeId)](tasksSeq.size())

    def containsTask = (task: Task) => tasksSeq.exists(x => x.value._1.id == task.id)
    val isReadyToRun = (task: T) => task.parents.forall(x => (mappedTasks.containsKey(x.id) && !containsTask(x)) || x.isInstanceOf[HeadDaxTask])

    while (tasksSeq.nonEmpty) {

      val el = tasksSeq.find( pair => {
        val (tsk, _) = pair.value
        isReadyToRun(tsk)
      }).get

      val (task, nodeId) = el.value
      repairedOrdering.add(el.value)
      mappedTasks.put(task.id, el.value)
      tasksSeq.remove(el)
    }

    repairedOrdering.toList
  }

  private class Pair[T](val num: Int, val value: T) extends Comparable[Pair[T]]{
    override def compareTo(o: Pair[T]): Int = num.compareTo(o.num)
  }
}