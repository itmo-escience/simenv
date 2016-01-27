package itmo.escience.simenv.algorithms.ga

import java.util

import itmo.escience.simenv.algorithms.ga.env.EnvConfSolution
import itmo.escience.simenv.algorithms.vm.env.EnvConfigurationProblem
import itmo.escience.simenv.environment.entities.{Context, Task, Node}
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
    val schedule = WorkflowSchedulingProblem.solutionToSchedule[T, N](s, ctx, environment)
    schedule.makespan()
  }
}