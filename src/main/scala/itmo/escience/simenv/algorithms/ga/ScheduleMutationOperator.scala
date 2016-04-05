package itmo.escience.simenv.algorithms.ga

import java.util
import java.util.Random

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.BasicContext
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.EvolutionaryOperator
import collection.JavaConversions._

/**
  * Created by mikhail on 22.01.2016.
  */
class ScheduleMutationOperator[T <: Task, N <: Node](ctx: Context[T, N], env: Environment[N],
                                                     probability: Double)
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

    val option = rnd.nextInt(5)

    option match {
      case 0 =>
        nodeReplace(mutant, rnd)
      case 1 =>  
        swapMutation(mutant, rnd)
      case 2 =>
        changeRel(mutant, rnd)
      case 3 =>
        addRepl(mutant, rnd)
      case 4 =>
        removeRepl(mutant, rnd)
    }

    mutant
  }

  private def nodeReplace(mutant:WFSchedSolution, rnd: Random) = {

    val liveNodes = ctx.environment.nodes.filter(x => x.status == NodeStatus.UP).toList
    val node = liveNodes(rnd.nextInt(liveNodes.length))

    val i = rnd.nextInt(mutant.getNumberOfVariables)
    val gene = mutant.getVariableValue(i)

    var newNode = 0
    if (mutant.maxNodeIdx > 0) {
      newNode = rnd.nextInt(mutant.maxNodeIdx)
    }
    if (rnd.nextDouble() < 0.2) {
      newNode = rnd.nextInt(mutant.maxNodeIdx + 1)
    }

    mutant.setVariableValue(i, MappedTask(gene.taskId, newNode, gene.rel))
  }

  private def swapMutation(mutant:WFSchedSolution, rnd: Random) = {

    val a = rnd.nextInt(mutant.getNumberOfVariables)
    val b = rnd.nextInt(mutant.getNumberOfVariables)

    val gene_1 = mutant.getVariableValue(a)
    val gene_2 = mutant.getVariableValue(b)

    mutant.setVariableValue(a, gene_2)
    mutant.setVariableValue(b, gene_1)
  }

  def changeRel(mutant:WFSchedSolution, rnd: Random) = {
    val rels = List[Double](0.7, 0.8, 0.85, 0.9, 0.95, 0.99)
    val idx = rnd.nextInt(mutant.getNumberOfVariables)
    val item = mutant.getVariableValue(idx)
    val newRel = rels(rnd.nextInt(rels.length))
    mutant.setVariableValue(idx, MappedTask(item.taskId, item.nodeIdx, newRel))
  }

  def addRepl(mutant:WFSchedSolution, rnd: Random) = {
    val rels = List[Double](0.7, 0.8, 0.85, 0.9, 0.95, 0.99)
    val relMap = new util.HashMap[String, Double]()
    val fixNodes = env.fixedNodes.length
    for (g <- mutant.genSeq) {
      if (!relMap.containsKey(g.taskId)) {
        relMap.put(g.taskId, 0.0)
      }
      val curRel = relMap.get(g.taskId)
      var nodeRel = ctx.asInstanceOf[BasicContext[DaxTask, CapacityBasedNode]].getFixRel
      if (g.nodeIdx >= fixNodes) {
        nodeRel = ctx.asInstanceOf[BasicContext[DaxTask, CapacityBasedNode]].getPubRel
      }
      relMap.put(g.taskId, curRel + (1 - curRel) * g.rel * nodeRel)
    }
    val keys = relMap.keySet().toList
    val lowTasks = keys.filter(x => relMap.get(x) < 0.99)
    if (lowTasks.nonEmpty && rnd.nextDouble() > 0.5) {
      val task = lowTasks(rnd.nextInt(lowTasks.length))

      mutant.insertGene(rnd.nextInt(mutant.getNumberOfVariables), new MappedTask(task, rnd.nextInt(mutant.maxNodeIdx), rels(rnd.nextInt(rels.length))))
    } else {
      val task = keys(rnd.nextInt(keys.length))

      mutant.insertGene(rnd.nextInt(mutant.getNumberOfVariables), new MappedTask(task, rnd.nextInt(mutant.maxNodeIdx), rels(rnd.nextInt(rels.length))))
    }
  }

  def removeRepl(mutant:WFSchedSolution, rnd: Random) = {
    val countMap = new util.HashMap[String, Int]()
    for (g <- mutant.genSeq) {
      if (!countMap.containsKey(g.taskId)) {
        countMap.put(g.taskId, 0)
      }
      countMap.put(g.taskId, countMap.get(g.taskId) + 1)
    }
    val keys = countMap.keySet().toList
    val availableTasks = keys.filter(x => countMap.get(x) > 1)
    if (availableTasks.nonEmpty) {
      val task = availableTasks(rnd.nextInt(availableTasks.length))
      val idx = mutant.genSeq.lastIndexWhere(x => x.taskId == task)
      mutant.removeGene(idx)
    }
  }
}
