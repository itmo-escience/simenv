package itmo.escience.simenv.algorithms.ga

import java.util

import itmo.escience.simenv.algorithms.ecgProcessing.{EcgEnvConfigurationProblem, EcgEnvConfSolution}
import itmo.escience.simenv.algorithms.ga.env.{EnvConfSolution, EnvConfigurationProblem}
import itmo.escience.simenv.environment.ecgProcessing.CoreStorageNode

//import itmo.escience.simenv.algorithms.ga.env.EnvConfSolution
import itmo.escience.simenv.environment.entities.{DaxTask, Context, Node, Task}
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.FitnessEvaluator

/**
  * Created by mikhail on 22.01.2016.
  */
class ScheduleFitnessEvaluator[T <: Task, N <: Node](ctx: Context[T, N], env: Environment[N]) extends FitnessEvaluator[WFSchedSolution] {
  override def isNatural: Boolean = false

  override def getFitness(t: WFSchedSolution, list: util.List[_ <: WFSchedSolution]): Double = {
    val schedule = WorkflowSchedulingProblem.solutionToSchedule[T, N](t, ctx, env)
    schedule.makespan()
  }

  def getFitness(s: WFSchedSolution, e: EnvConfSolution): Double = {
    val environment = EnvConfigurationProblem.solutionToEnvironment[T, N](e, ctx)
    val schedule = WorkflowSchedulingProblem.coevSolutionToSchedule[T, N](s, ctx, environment)
    schedule.makespan()
  }

  def getFitness(s: WFSchedSolution, e: EcgEnvConfSolution): Double = {
    val environment = EcgEnvConfigurationProblem.solutionToEnvironment(e, ctx.asInstanceOf[Context[DaxTask, CoreStorageNode]])
    val schedule = WorkflowSchedulingProblem.ecgSolutionToSchedule[T, N](s, ctx, environment.asInstanceOf[Environment[N]])
    schedule.makespan()
  }

}