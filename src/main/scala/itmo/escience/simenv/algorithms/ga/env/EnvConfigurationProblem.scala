package itmo.escience.simenv.algorithms.ga.env

import java.util

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.{BasicContext, BasicEnvironment}
import itmo.escience.simenv.environment.modelling.Environment

/**
 * Created by user on 02.12.2015.
 */

object EnvConfigurationProblem {

  def environmentToSolution[N <: Node](env: Environment[N]):EnvConfSolution = {
    val genes: List[MappedEnv] = env.asInstanceOf[BasicEnvironment].publicNodes.map(x => new MappedEnv(x.capacity)).toList
    new EnvConfSolution(genes, env.fixedNodes.length)
  }

  def solutionToEnvironment[T <: Task, N <: Node](solution: EnvConfSolution, context: Context[T, N]): Environment[N] = {
    // reconstruction of environment in context with new configuration of vms

    var fixNodes = List[CapacityBasedNode]()
    var pubNodes = List[CapacityBasedNode]()

    for (n <- context.environment.fixedNodes) {
      val newNode = n.asInstanceOf[CapacityBasedNode].copy(n.asInstanceOf[CapacityBasedNode].reliability, isFixed = true)
      fixNodes :+= newNode
    }

    var i = fixNodes.length
    for (n <- solution.genSeq) {
      val res: CapacityBasedNode = new CapacityBasedNode(id=s"rep_$i", name=s"rep_$i",
        capacity=n.capacity, reliability = context.asInstanceOf[BasicContext[DaxTask, CapacityBasedNode]].getPubRel, fixed = false)
      pubNodes :+= res
      i += 1
    }

    val environment: Environment[N] = new BasicEnvironment(fixNodes, pubNodes, List[Network](), context.environment.asInstanceOf[BasicEnvironment].getTypes).asInstanceOf[Environment[N]]
    environment
  }
}


