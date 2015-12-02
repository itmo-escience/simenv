package itmo.escience.simenv.algorithms.ga

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.SingleAppWorkload
import org.uma.jmetal.problem.Problem

/**
 * Created by user on 02.12.2015.
 */
class WorkflowSchedulingProblem(wf:Workflow, newSchedule:Schedule, nodes:Seq[CapacityBasedNode], context:Context[DaxTask, CapacityBasedNode]) extends Problem[WorkflowSchedulingSolution]{

  override def getNumberOfObjectives: Int = 1

  override def getNumberOfConstraints: Int = 0

  override def getName: String = "WorkflowSchedulingProblem"

  override def evaluate(s: WorkflowSchedulingSolution): Unit = {
    throw new NotImplementedError()
  }

  override def getNumberOfVariables: Int = wf.tasks.length

  override def createSolution(): WorkflowSchedulingSolution = {
    // schedule = RandomScheduler.schedule()
    // convert to chromosome
    // return it
    throw new NotImplementedError()
  }
}
