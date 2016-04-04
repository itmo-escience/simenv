package itmo.escience.simenv.algorithms.ga

import java.util.Random

import itmo.escience.simenv.algorithms.RandomScheduler
import itmo.escience.simenv.environment.entities.{Context, Node, Task}
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory

/**
  * Created by mikhail on 22.01.2016.
  */
class FixScheduleCandidateFactory[T <: Task, N <: Node](ctx: Context[T, N], env: Environment[N]) extends AbstractCandidateFactory[WFSchedSolution]{

  override def generateRandomCandidate(random: Random): WFSchedSolution = {
    val schedule = RandomScheduler.schedule[T, N](ctx, env)
    val solution = WorkflowSchedulingProblem.scheduleToSolution[T, N](schedule, ctx, env)
    solution
  }
}
