package itmo.escience.simenv.algorithms.ultraGA

import java.util

import itmo.escience.simenv.environment.entities.{Context, Node, Task}
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.FitnessEvaluator

/**
  * Created by mikhail on 22.01.2016.
  */
class MishanyaScheduleFitnessEvaluator[T <: Task, N <: Node](ctx: Context[T, N], env: Environment[N]) extends FitnessEvaluator[MishanyaSolution] {
  override def isNatural: Boolean = false

  override def getFitness(t: MishanyaSolution, list: util.List[_ <: MishanyaSolution]): Double = {
    val schedule = MishanyaWorkflowSchedulingProblem.solutionToSchedule[T, N](t, ctx, env)
    schedule.makespan()
  }

}