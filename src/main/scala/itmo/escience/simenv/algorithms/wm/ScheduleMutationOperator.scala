package itmo.escience.simenv.algorithms.wm

import java.util
import java.util.Random

import itmo.escience.simenv.environment.entities.{Node, Task, Context, NodeStatus}
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

    val liveNodes = ctx.environment.nodes.filter(x => x.status == NodeStatus.UP).toList
    val node = liveNodes(rnd.nextInt(liveNodes.length))

    val i = rnd.nextInt(mutant.getNumberOfVariables)
    val gene = mutant.getVariableValue(i)

    mutant.setVariableValue(i, MappedTask(gene.taskId, rnd.nextInt(mutant.maxNodeIdx)))
  }

  private def doSwapMutation(mutant:WFSchedSolution, rnd: Random) = {

    val a = rnd.nextInt(mutant.getNumberOfVariables)
    val b = rnd.nextInt(mutant.getNumberOfVariables)

    val gene_1 = mutant.getVariableValue(a)
    val gene_2 = mutant.getVariableValue(b)

    mutant.setVariableValue(a, MappedTask(gene_1.taskId, gene_2.nodeIdx))
    mutant.setVariableValue(b, MappedTask(gene_2.taskId, gene_1.nodeIdx))
  }
}
