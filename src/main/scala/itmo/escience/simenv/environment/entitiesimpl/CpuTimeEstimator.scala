package itmo.escience.simenv.environment.entitiesimpl

import itmo.escience.simenv.environment.entities.{CpuTimeNode, DaxTask, Network, Node}
import itmo.escience.simenv.environment.modelling.{Environment, Estimator}

/**
 * Created by Nikolay on 11/29/2015.
 */
class CpuTimeEstimator(env: Environment[CpuTimeNode]) extends Estimator[DaxTask, CpuTimeNode]{

  override def calcTime(task: DaxTask, node: CpuTimeNode): Double = {
    task.execTime / (node.cpu * node.cpuTime / 100)
  }

  override def calcTransferTime(from: (DaxTask, CpuTimeNode), to: (DaxTask, CpuTimeNode)): Double = {
    val (parent_task, from_node) = from
    val (child_task, to_node) = to

    if (from_node.id == to_node.id) {
      return 0.0
    }

    val from_networks = env.networksByNode(from_node)
    val to_networks = env.networksByNode(to_node)

    // TODO: ATTENTION! intersection is possible if the entity of network is immutable
    val transferNetwork = from_networks.intersect(to_networks).max(new Ordering[Network] {
      override def compare(x: Network, y: Network): Int = x.bandwidth.compare(y.bandwidth)
    })

    val volume = child_task.volumeToTransfer(parent_task)

    //estimate time
    volume / transferNetwork.bandwidth
  }
}
