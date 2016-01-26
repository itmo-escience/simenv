package itmo.escience.simenv.algorithms.wm.env

import java.util.Random

import itmo.escience.simenv.environment.entities.{Context, Node, Task}
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory

/**
  * Created by mikhail on 26.01.2016.
  */
class EnvCandidateFactory[T <: Task, N <: Node](ctx: Context[T, N], env: Environment[N], types: List[Double]) extends AbstractCandidateFactory[EnvConfSolution] {
  override def generateRandomCandidate(random: Random): EnvConfSolution = {
    val tasksCount = ctx.workload.apps.foldLeft(0)((s, x) => s + x.tasks.size)
    val nodesNumber = random.nextInt(tasksCount / 5) + 1
    generateRandomCandidate(random, nodesNumber)
  }

  def generateRandomCandidate(random: Random, nodesNumber: Int): EnvConfSolution = {
    var sol: List[MappedEnv] = List[MappedEnv]()
    for (i <- 0 until nodesNumber) {
      sol :+= new MappedEnv(types(random.nextInt(types.size)))
    }
    new EnvConfSolution(sol)
  }

}
