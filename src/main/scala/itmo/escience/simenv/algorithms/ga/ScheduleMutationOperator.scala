package itmo.escience.simenv.algorithms.ga

import java.util
import java.util.Random

import itmo.escience.simenv.algorithms.ga.env.EnvConfSolution
import itmo.escience.simenv.environment.entities.{Context, Node, NodeStatus, Task}
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.EvolutionaryOperator

/**
  * Created by mikhail on 22.01.2016.
  */
class ScheduleMutationOperator[T <: Task, N <: Node](ctx: Context[T, N], env: Environment[N],
                                                     probability: Double, swapProbability: Double)
                                                      extends EvolutionaryOperator[WFSchedSolution]{

  override def apply(mutants: util.List[WFSchedSolution], random: Random): util.List[WFSchedSolution] = {
    val mutatedPopulation: util.ArrayList[WFSchedSolution] = new util.ArrayList[WFSchedSolution](mutants.size())
    val it: util.Iterator[WFSchedSolution] = mutants.iterator()

    while(it.hasNext) {
      val s: WFSchedSolution = it.next()
      mutatedPopulation.add(mutateSolution(s, random))
    }
    mutatedPopulation
  }

  def mutateSolution(mutant: WFSchedSolution, rnd: Random): WFSchedSolution = {
    if (rnd.nextDouble() <= probability) {
      doMutation(mutant, rnd)
    }

    if (rnd.nextDouble() <= swapProbability) {
      doSwapMutation(mutant, rnd)
    }
    mutant
  }

  private def doMutation(mutant:WFSchedSolution, rnd: Random) = {

    val liveNodes = env.nodes.filter(x => x.status == NodeStatus.UP).toList
    val node = liveNodes(rnd.nextInt(liveNodes.length))

    val i = rnd.nextInt(mutant.getNumberOfVariables)
    val gene = mutant.getVariableValue(i)

    mutant.setVariableValue(i, MappedTask(gene.taskId, node.id))
  }

  private def doSwapMutation(mutant:WFSchedSolution, rnd: Random) = {

    val a = rnd.nextInt(mutant.getNumberOfVariables)
    val b = rnd.nextInt(mutant.getNumberOfVariables)

    val gene_1 = mutant.getVariableValue(a)
    val gene_2 = mutant.getVariableValue(b)

    mutant.setVariableValue(a, gene_2)
    mutant.setVariableValue(b, gene_1)
  }

}
