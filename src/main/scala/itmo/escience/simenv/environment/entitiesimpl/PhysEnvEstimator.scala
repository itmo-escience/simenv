package itmo.escience.simenv.environment.entitiesimpl

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.modelling.{Environment, Estimator}


/**
 * Created by Nikolay on 11/29/2015.
 */
class PhysEnvEstimator(env: Environment[CoreRamHddBasedNode]) extends Estimator[DaxTask, CoreRamHddBasedNode]{

  override def calcTime(task: DaxTask, node: CoreRamHddBasedNode): Double = {
    task.execTime / node.cores
  }

  override def calcTransferTime(from: (DaxTask, CoreRamHddBasedNode), to: (DaxTask, CoreRamHddBasedNode)): Double = {
    val (parent_task, from_node) = from
    val (child_task, to_node) = to

    if (from_node.parent == to_node.parent) {
      return 0.0
    }
    val fromNodeRes = env.nodeOrContainerById(from_node.parent).asInstanceOf[CoreRamHddBasedNode]
    val toNodeRes = env.nodeOrContainerById(to_node.parent).asInstanceOf[CoreRamHddBasedNode]

    val from_networks = env.networksByNode(fromNodeRes)
    val to_networks = env.networksByNode(toNodeRes)

    // TODO: ATTENTION! intersection is possible if the entity of network is immutable
    val transferNetwork = from_networks.intersect(to_networks).max(new Ordering[Network] {
      override def compare(x: Network, y: Network): Int = x.bandwidth.compare(y.bandwidth)
    })

    val volume = child_task.volumeToTransfer(parent_task)

    //estimate time
    volume / transferNetwork.bandwidth
  }
}
