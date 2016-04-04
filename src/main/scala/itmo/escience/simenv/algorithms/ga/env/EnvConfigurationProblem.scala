package itmo.escience.simenv.algorithms.vm.env

import java.util

import itmo.escience.simenv.algorithms.RandomScheduler
import itmo.escience.simenv.algorithms.ga.env.{MappedEnv, EnvConfSolution}
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.BasicEnvironment
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.utilities.Units._
import itmo.escience.simenv.utilities.Utilities._

/**
 * Created by user on 02.12.2015.
 */

object EnvConfigurationProblem {

  def environmentToSolution[N <: Node](env: Environment[N]):EnvConfSolution = {
    val genes: List[MappedEnv] = env.nodes.map(x => new MappedEnv(x.asInstanceOf[CapacityBasedNode].capacity)).toList
    new EnvConfSolution(genes)
  }

  def solutionToEnvironment[T <: Task, N <: Node](solution: EnvConfSolution, context: Context[T, N]): Environment[N] = {
    // reconstruction of environment in context with new configuration of vms
    var newNodes: List[CapacityBasedNode] = List()
    var i = 0
    for (n <- solution.genSeq) {
      val res: CapacityBasedNode = new CapacityBasedNode(id=s"res_$i", name=s"res_$i",
        capacity=n.capacity)
      newNodes :+= res
      i += 1
    }

    //TODO DANGER!!!!!!!!!!!
    val bandwidth = 100 Mbit_Sec
    val networks = List(new Network(id=generateId(), name="", bandwidth=bandwidth, newNodes))
    val environment: Environment[N] = new BasicEnvironment(newNodes, Seq[CapacityBasedNode](), networks, context.environment.asInstanceOf[BasicEnvironment].getTypes).asInstanceOf[Environment[N]]

    environment
  }
}


