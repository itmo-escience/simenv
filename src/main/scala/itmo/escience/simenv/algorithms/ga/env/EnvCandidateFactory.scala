package itmo.escience.simenv.algorithms.ga.env

import java.util.Random

import itmo.escience.simenv.environment.entities.{Context, Node, Task}
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory

/**
  * Created by mikhail on 26.01.2016.
  */
class EnvCandidateFactory[T <: Task, N <: Node](ctx: Context[T, N], env: Environment[N], types: List[Double]) extends AbstractCandidateFactory[EnvConfSolution] {
  override def generateRandomCandidate(random: Random): EnvConfSolution = {
    val fixSize= env.fixedNodes.length
    val pubSize = env.publicNodes.length
    val nodesNumber = random.nextInt(pubSize) + 1
    generateRandomCandidate(random, nodesNumber, fixSize)
  }

  def generateRandomCandidate(random: Random, nodesNumber: Int, fixSize: Int): EnvConfSolution = {
    var sol: List[MappedEnv] = List[MappedEnv]()
    for (i <- 0 until nodesNumber) {
      sol :+= new MappedEnv(types(random.nextInt(types.size)))
    }
    new EnvConfSolution(sol, fixSize)
  }

}
