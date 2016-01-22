package itmo.escience.simenv.algorithms.wm

import java.util
import java.util.Random

import itmo.escience.simenv.algorithms.RandomScheduler
import itmo.escience.simenv.environment.entities.{Task, Node, DaxTask, Context}
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.CandidateFactory
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory

/**
  * Created by mikhail on 22.01.2016.
  */
class ScheduleCandidateFactory[T <: Task, N <: Node](ctx: Context[T, N], env: Environment[N]) extends AbstractCandidateFactory[WorkflowSchedulingSolution]{

  override def generateRandomCandidate(random: Random): WorkflowSchedulingSolution = {
    val schedule = RandomScheduler.schedule[T, N](ctx, env)
    val solution = WorkflowSchedulingProblem.scheduleToSolution(schedule, ctx)
    solution
  }
}
