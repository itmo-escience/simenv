package itmo.escience.simenv.algorithms.ecgProcessing

import itmo.escience.simenv.environment.ecgProcessing.{CoreStorageCarrier, CoreStorageNode}
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.CarrierNodeEnvironment
import itmo.escience.simenv.environment.modelling.Environment

/**
 * Created by user on 02.12.2015.
 */

object EcgEnvConfigurationProblem {

  def environmentToSolution(env: CarrierNodeEnvironment[CoreStorageNode]):EcgEnvConfSolution = {
    val genes: List[EcgMappedEnv] = env.nodes.filter(x => x.status == NodeStatus.UP).map(x => new EcgMappedEnv(x.id, x.cores)).toList
    new EcgEnvConfSolution(genes)
  }

  def solutionToEnvironment(solution: EcgEnvConfSolution, context: Context[DaxTask, CoreStorageNode]): CarrierNodeEnvironment[CoreStorageNode] = {
    // reconstruction of environment in context with new configuration of vms
    val carriers = context.environment.carriers.map(x => x.asInstanceOf[CoreStorageCarrier])
    val vms = context.environment.nodes.asInstanceOf[List[CoreStorageNode]]
    var newNodes: List[CoreStorageCarrier] = List()
    for (n <- carriers) {
      val res: CoreStorageCarrier = new CoreStorageCarrier(id=n.id, name=n.name,
        cores = n.cores, files=n.files,
        reliability=n.reliability
      )
      for (vm <- vms.filter(x => x.parent == res.id)) {
        if (vm.status == NodeStatus.UP) {
          val vmItem = solution.getVmElement(vm.id)
          res.addChild(new CoreStorageNode(id = vm.id, name = vm.name, cores = vmItem.cores, parent = res.id, reliability = vm.reliability))
        } else {
          val vmItem = vm
          res.addChild(new CoreStorageNode(id = vm.id, name = vm.name, cores = vmItem.cores, parent = res.id, reliability = vm.reliability, status=NodeStatus.DOWN))
        }
      }
      newNodes :+= res
    }

    val networks = context.environment.networks
    val environment: CarrierNodeEnvironment[CoreStorageNode] = new CarrierNodeEnvironment[CoreStorageNode](newNodes, networks)

    environment
  }
}


