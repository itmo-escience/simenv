package itmo.escience.simenv.algorithms.ultraGA

import java.util.Random

import itmo.escience.simenv.algorithms.RandomScheduler
import itmo.escience.simenv.environment.entities.{Context, Node, Task}
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory

/**
  * Created by mikhail on 22.01.2016.
  */
class MishanyaScheduleCandidateFactory[T <: Task, N <: Node](ctx: Context[T, N], env: Environment[N]) extends AbstractCandidateFactory[MishanyaSolution]{

  override def generateRandomCandidate(random: Random): MishanyaSolution = {
    val schedule = RandomScheduler.schedule[T, N](ctx, env)
    val solution = MishanyaWorkflowSchedulingProblem.scheduleToSolution[T, N](schedule, ctx, env)
    solution
  }
}
