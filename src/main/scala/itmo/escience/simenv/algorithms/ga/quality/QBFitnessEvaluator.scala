package itmo.escience.simenv.algorithms.ga.quality

import java.util

import itmo.escience.simenv.environment.entities.{Context, Node, Task, Schedule}
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.FitnessEvaluator

/**
  * Created by user on 17.02.2016.
  */
class QBFitnessEvaluator extends FitnessEvaluator[QBScheduleSolution]{
  throw new NotImplementedError()

  override def isNatural: Boolean = ???

  override def getFitness(t: QBScheduleSolution, list: util.List[_ <: QBScheduleSolution]): Double = ???
}

object QBFitnessEvaluator {
  def solutionToSchedule[T <: Task, N <: Node](solution:QBScheduleSolution,
                                               context:Context[T, N],
                                               environment:Environment[N]): Schedule[T, N] = ???
}
