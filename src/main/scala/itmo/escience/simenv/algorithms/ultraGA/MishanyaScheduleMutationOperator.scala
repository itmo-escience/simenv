package itmo.escience.simenv.algorithms.ultraGA

import java.util
import java.util.Random

import itmo.escience.simenv.environment.entities.{Context, Node, NodeStatus, Task}
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.EvolutionaryOperator

/**
  * Created by mikhail on 22.01.2016.
  */
class MishanyaScheduleMutationOperator[T <: Task, N <: Node](ctx: Context[T, N], env: Environment[N],
                                                             probability: Double, swapProbability: Double)
                                                      extends EvolutionaryOperator[MishanyaSolution]{

  override def apply(mutants: util.List[MishanyaSolution], random: Random): util.List[MishanyaSolution] = {
    val mutatedPopulation: util.ArrayList[MishanyaSolution] = new util.ArrayList[MishanyaSolution](mutants.size())
    val it: util.Iterator[MishanyaSolution] = mutants.iterator()

    while(it.hasNext) {
      val s: MishanyaSolution = it.next()
      mutatedPopulation.add(mutateSolution(s, random))
    }
    mutatedPopulation
  }

  def mutateSolution(mutant: MishanyaSolution, rnd: Random): MishanyaSolution = {
    if (rnd.nextDouble() <= probability) {
      doMutation(mutant, rnd)
      mutant.evaluated = false
    }
    mutant
  }

  private def doMutation(mutant:MishanyaSolution, rnd: Random) = {
//    for (i <- mutant.genSeq.indices) {

      val i = rnd.nextInt(mutant.getNumberOfVariables)
//      if (rnd.nextDouble() < 0.2) {
        val gene = mutant.getVariableValue(i)
        val oldNode = gene.nodeId

        val liveNodes = env.nodes.filter(x => x.status == NodeStatus.UP && x.id != oldNode).toList
        if (liveNodes.nonEmpty) {
          val node = liveNodes(rnd.nextInt(liveNodes.length))
          mutant.setVariableValue(i, MMappedTask(gene.taskId, node.id))
        }
//      }
//    }
  }

  private def doSwapMutation(mutant:MishanyaSolution, rnd: Random) = {

    val a = rnd.nextInt(mutant.getNumberOfVariables)
    val b = rnd.nextInt(mutant.getNumberOfVariables)

    val gene_1 = mutant.getVariableValue(a)
    val gene_2 = mutant.getVariableValue(b)

    mutant.setVariableValue(a, gene_2)
    mutant.setVariableValue(b, gene_1)
  }

}
