package itmo.escience.simenv.algorithms.ga.env

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.{CarrierNodeEnvironment, BasicEnvironment}
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.utilities.Units._
import itmo.escience.simenv.utilities.Utilities._

/**
 * Created by user on 02.12.2015.
 */

object EnvConfigurationProblem {

  def environmentToSolution[N <: Node](env: Environment[N]):EnvConfSolution = {
    val genes: List[MappedEnv] = env.nodes.map(x => new MappedEnv(x.id, x.asInstanceOf[CapacityBasedNode].capacity)).toList
    new EnvConfSolution(genes)
  }

  def solutionToEnvironment[T <: Task, N <: Node](solution: EnvConfSolution, context: Context[T, N]): Environment[N] = {
    // reconstruction of environment in context with new configuration of vms
    val carriers = context.environment.carriers.map(x => x.asInstanceOf[CapacityBasedCarrier])
    val vms = context.environment.nodes.asInstanceOf[List[CapacityBasedNode]]
    var newNodes: List[CapacityBasedCarrier] = List()
    for (n <- carriers) {
      val res: CapacityBasedCarrier = new CapacityBasedCarrier(id=n.id, name=n.name,
        capacity = n.capacity,
        reliability=n.reliability
      )
      for (vm <- vms.filter(x => x.parent == res.id)) {
        val vmItem = solution.getVmElement(vm.id)
        res.addChild(new CapacityBasedNode(id=vm.id, name=vm.name, capacity=vmItem.cap, parent=res.id, reliability=vm.reliability))
      }
      newNodes :+= res
    }

    val networks = context.environment.networks
    val environment: CarrierNodeEnvironment[CapacityBasedNode] = new CarrierNodeEnvironment[CapacityBasedNode](newNodes, networks)

    environment.asInstanceOf[Environment[N]]
  }
}


