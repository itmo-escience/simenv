package itmo.escience.simenv.algorithms.ga.env

import java.util
import java.util.Random

import itmo.escience.simenv.environment.entities.{Node, Task, Context}
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.EvolutionaryOperator

/**
  * Created by mikhail on 27.01.2016.
  */
class EnvMutationOperator[T <: Task, N <: Node](ctx: Context[T, N], env: Environment[N],
                          probability: Double, nodesTypes: List[Double])
  extends EvolutionaryOperator[EnvConfSolution] {

  override def apply(mutants: util.List[EnvConfSolution], random: Random): util.List[EnvConfSolution] = {
    val mutatedPopulation: util.ArrayList[EnvConfSolution] = new util.ArrayList[EnvConfSolution](mutants.size())
    val it: util.Iterator[EnvConfSolution] = mutants.iterator()

    while(it.hasNext) {
      val s: EnvConfSolution = it.next()
      mutatedPopulation.add(mutateSolution(s, random))
    }
    mutatedPopulation
  }

  def mutateSolution(mutant: EnvConfSolution, rnd: Random): EnvConfSolution = {
    if (rnd.nextDouble() <= probability) {
      doMutation(mutant, rnd)
    }

    mutant
  }

  def doMutation(mutant:EnvConfSolution, rnd: Random) = {
    var options = List[Int](13, 666)
    if (mutant.genSeq.size > 1) {
      options :+= 21
    }
    val option: Int = options(rnd.nextInt(options.size))
    option match {
      case 13 => changeNode(mutant, rnd)
      case 666 => addNode(mutant, rnd)
      case 21 => deleteNode(mutant, rnd)
    }
    mutant
  }

  def changeNode(mutant:EnvConfSolution, rnd: Random) = {
    if (mutant.getNumberOfVariables > 0) {
      val idx = rnd.nextInt(mutant.getNumberOfVariables)
      val newCap = nodesTypes(rnd.nextInt(nodesTypes.size))
      mutant.setVariableValue(idx, new MappedEnv(newCap))
    }
  }

  def addNode(mutant:EnvConfSolution, rnd: Random) = {
    mutant.addValue(nodesTypes(rnd.nextInt(nodesTypes.size)))
  }

  def deleteNode(mutant:EnvConfSolution, rnd: Random) = {
    mutant.deleteValue(rnd.nextInt(mutant.getNumberOfVariables))
  }
}
