package itmo.escience.simenv.algorithms.ga

import java.util
import java.util.Map.Entry

import itmo.escience.simenv.algorithms.RandomScheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.SingleAppWorkload
import org.uma.jmetal.problem.Problem
import scala.collection.JavaConversions._

import scala.util.Random
import scala.collection.JavaConversions._

/**
 * Created by user on 02.12.2015.
 */

object WorkflowSchedulingProblem {

  // TODO: ATTENTION! Now it does NOT work for dynamic case. It needs to be implemented. Context will be needed for it
  def scheduleToSolution(schedule:Schedule, context: Context[DaxTask, CapacityBasedNode]):WorkflowSchedulingSolution = {
    //TODO: implement dealing with dynamics
    val taskItems = schedule.scheduleItemsSeq().filter({
      case x: TaskScheduleItem => true
      case _ => false
    }).map(x => x.asInstanceOf[TaskScheduleItem])

    val genes = taskItems.map(x => MappedTask(x.task.id, x.node.id)).toList

    new WorkflowSchedulingSolution(genes)
  }

  def solutionToSchedule(solution: WorkflowSchedulingSolution, context: Context[DaxTask, CapacityBasedNode]): Schedule = {
    //TODO: implement dealing with dynamics
    val newSchedule = Schedule.emptySchedule()

    // repair sequence in relation with parent-child dependencies
    // construct new schedule by placing it in task-by-task manner

    val repairedOrdering = repairOrdering(solution, context)

    for (x <- repairedOrdering) {
      val (task, nodeId) = x
      newSchedule.placeTask(task,
        context.environment.nodeOrContainerById(nodeId).asInstanceOf[CapacityBasedNode],
        context)
    }

    newSchedule
  }

  private def repairOrdering(solution: WorkflowSchedulingSolution, context: Context[DaxTask, CapacityBasedNode]):List[(DaxTask, NodeId)] = {
    val wf = context.workload.asInstanceOf[SingleAppWorkload].app
    val tasksSeq = new util.TreeSet[Pair[(DaxTask, NodeId)]](solution.tasksSeq().zipWithIndex
      .map( { case (x, i) =>
        new Pair(i,
          (wf.taskById(x.taskId).asInstanceOf[DaxTask], x.nodeId)
        )}
      ))

    val mappedTasks = new util.HashMap[TaskId, (DaxTask, NodeId)]()
    val repairedOrdering: java.util.ArrayList[(DaxTask, NodeId)] = new util.ArrayList[(DaxTask, NodeId)](tasksSeq.size())
//    val isReadyToRun = (task: DaxTask) => if (task.parents.size == 1 && task.parents.head.isInstanceOf[HeadDaxTask])
//      true else task.parents.forall(x => mappedTasks.containsKey(x.id) )
    val isReadyToRun = (task: DaxTask) => task.parents.forall(x => mappedTasks.containsKey(x.id) || x.isInstanceOf[HeadDaxTask])

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

class WorkflowSchedulingProblem(wf:Workflow, newSchedule:Schedule, nodes:Seq[CapacityBasedNode], context:Context[DaxTask, CapacityBasedNode]) extends Problem[WorkflowSchedulingSolution]{

  override def getNumberOfObjectives: Int = 1

  override def getNumberOfConstraints: Int = 0

  override def getName: String = "WorkflowSchedulingProblem"

  override def evaluate(s: WorkflowSchedulingSolution): Unit = {
    val schedule = WorkflowSchedulingProblem.solutionToSchedule(s, context)
    val makespan = schedule.makespan()
    s.setObjective(0, makespan)
  }

  override def getNumberOfVariables: Int = wf.tasks.length

  override def createSolution(): WorkflowSchedulingSolution = {
    // schedule = RandomScheduler.schedule()
    // convert to chromosome
    // return it
    val schedule = RandomScheduler.schedule(context)

    val solution = WorkflowSchedulingProblem.scheduleToSolution(schedule, context)
    solution
  }
}

