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

  def apply(mutants: util.List[WFSchedSolution], perfEnv: EnvConfSolution, random: Random): util.List[WFSchedSolution] = {
    val mutatedPopulation: util.ArrayList[WFSchedSolution] = new util.ArrayList[WFSchedSolution](mutants.size())
    val it: util.Iterator[WFSchedSolution] = mutants.iterator()

    while(it.hasNext) {
      val s: WFSchedSolution = it.next()
      mutatedPopulation.add(mutateSolution(s, perfEnv, random))
    }
    mutatedPopulation
  }

  def mutateSolution(mutant: WFSchedSolution, perfEnv: EnvConfSolution, rnd: Random): WFSchedSolution = {
    if (rnd.nextDouble() <= probability) {
      doMutation(mutant, rnd)
    }

    if (rnd.nextDouble() <= swapProbability) {
      doSwapMutation(mutant, rnd)
    }

    mutant
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

//  private def doMutation(mutant:WFSchedSolution, perfEnv: EnvConfSolution, rnd: Random) = {
//
//    val liveNodes = env.nodes.filter(x => x.status == NodeStatus.UP).toList
//    var node = liveNodes(rnd.nextInt(liveNodes.length))
//    if (rnd.nextBoolean()) {
//      val notZeroNodes = liveNodes.filter(x => perfEnv.getVmElement(x.id).cap > 0)
//      node = notZeroNodes(rnd.nextInt(notZeroNodes.size))
//    }
//
//    val i = rnd.nextInt(mutant.getNumberOfVariables)
//    val gene = mutant.getVariableValue(i)
//
//    mutant.setVariableValue(i, MappedTask(gene.taskId, node.id))
//  }

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

    mutant.setVariableValue(a, MappedTask(gene_1.taskId, gene_2.nodeId))
    mutant.setVariableValue(b, MappedTask(gene_2.taskId, gene_1.nodeId))
  }

  override def apply(mutants: util.List[WFSchedSolution], random: Random): util.List[WFSchedSolution] = {
    val mutatedPopulation: util.ArrayList[WFSchedSolution] = new util.ArrayList[WFSchedSolution](mutants.size())
    val it: util.Iterator[WFSchedSolution] = mutants.iterator()

    while(it.hasNext) {
      val s: WFSchedSolution = it.next()
      mutatedPopulation.add(mutateSolution(s, random))
    }
    mutatedPopulation
  }
}
