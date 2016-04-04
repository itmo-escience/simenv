package itmo.escience.simenv.algorithms.ga

import java.util.Random

import itmo.escience.simenv.algorithms.RandomScheduler
import itmo.escience.simenv.environment.entities.{Task, Node, Context}
import itmo.escience.simenv.environment.entitiesimpl.BasicEnvironment
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory

/**
  * Created by mikhail on 22.01.2016.
  */
class ScheduleCandidateFactory[T <: Task, N <: Node](ctx: Context[T, N], env: Environment[N]) extends AbstractCandidateFactory[WFSchedSolution]{

  override def generateRandomCandidate(random: Random): WFSchedSolution = {
    val schedule = RandomScheduler.schedule[T, N](ctx, env)
    val solution = WorkflowSchedulingProblem.scheduleToSolution[T, N](schedule, ctx, env)
    val nodesCount = env.nodes.length
    val genSeq = solution.genSeq
    var newGenes = List[MappedTask]()
    val rels = List[Double](0.7, 0.8, 0.85, 0.9, 0.95, 0.99)
    for (gene <- genSeq) {
      val newItem = new MappedTask(gene.taskId, gene.nodeIdx, rels(random.nextInt(rels.length)))
      newGenes :+= newItem
      val nodeRel = env.asInstanceOf[BasicEnvironment].getRelByIdx(gene.nodeIdx)
      val taskRel = newItem.rel
      var rel = nodeRel * taskRel
      while (rel < 0.99) {
        val newItem = new MappedTask(gene.taskId, random.nextInt(nodesCount), rels(random.nextInt(rels.length)))
        val nodeRel = env.asInstanceOf[BasicEnvironment].getRelByIdx(newItem.nodeIdx)
        val taskRel = newItem.rel
        rel = rel + (1-rel) * nodeRel*taskRel
        newGenes :+= newItem
      }
    }
    val newSol = new WFSchedSolution(newGenes)
    newSol
//    solution
  }
}
