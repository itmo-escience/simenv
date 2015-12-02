package itmo.escience.simenv.algorithms.ga

import java.util
import java.util.Map.Entry

import itmo.escience.simenv.algorithms.RandomScheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.SingleAppWorkload
import org.uma.jmetal.problem.Problem
import scala.collection.JavaConversions._

import scala.util.Random

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

    // java.util.ArrayList[Any] ==


    val wf = context.workload.asInstanceOf[SingleAppWorkload].app
    val tasksSeq = new util.TreeSet[(DaxTask, NodeId)](solution.tasksSeq().map(x => (wf.taskById(x.taskId).asInstanceOf[DaxTask], x.nodeId)))

    val orderedMappedTasks = new util.TreeMap[TaskId, (DaxTask, NodeId)]()
    val isReadyToRun = (task: DaxTask) => task.parents.forall(x => orderedMappedTasks.containsKey(x.id))

    while (tasksSeq.nonEmpty) {
      val el = tasksSeq.find({ case (tsk, _ ) => isReadyToRun(tsk)}).get
      val (task, nodeId) = el
      orderedMappedTasks.put(task.id, el)
      tasksSeq.remove(el)
    }

    val ordering:List[(TaskId, (DaxTask, NodeId))] = orderedMappedTasks.toList

    for (x <- ordering) {
      val (taskId, (task, nodeId)) = x
      newSchedule.placeTask(task,
        context.environment.nodeOrContainerById(nodeId).asInstanceOf[CapacityBasedNode],
        context)
    }

    newSchedule
  }
}

class WorkflowSchedulingProblem(wf:Workflow, newSchedule:Schedule, nodes:Seq[CapacityBasedNode], context:Context[DaxTask, CapacityBasedNode]) extends Problem[WorkflowSchedulingSolution]{

  override def getNumberOfObjectives: Int = 1

  override def getNumberOfConstraints: Int = 0

  override def getName: String = "WorkflowSchedulingProblem"

  override def evaluate(s: WorkflowSchedulingSolution): Unit = {
    val schedule = WorkflowSchedulingProblem.solutionToSchedule(s, context)
    val makespan = schedule.makespan()
    throw new NotImplementedError()
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

