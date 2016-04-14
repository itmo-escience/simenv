package itmo.escience.simenv.environment.entitiesimpl

import itmo.escience.simenv.environment.entities.{DetailedNode, CapacityBasedNode, DaxTask}
import itmo.escience.simenv.environment.modelling.{Environment, Estimator}

/**
 * Created by Nikolay on 11/29/2015.
 */
class IPEstimator(env: IPEnvironment) extends Estimator[DaxTask, DetailedNode]{

  override def calcTime(task: DaxTask, node: DetailedNode): Double = {
    if (node.cpu == 0) {
      return task.execTime * 666
    }
    node.cpu * task.execTime
//  task.execTime / math.log(node.capacity + 1)
  }

  override def calcTransferTime(from: (DaxTask, DetailedNode), to: (DaxTask, DetailedNode)): Double = {
    val (parent_task, from_node) = from
    val (child_task, to_node) = to

    if (from_node.id == to_node.id) {
      0.0
    }
    if (from_node.parent == to_node.parent) {
      10.0
    } else
    {
    100.0
    }


//    val from_networks = env.networksByNode(from_node)
//    val to_networks = env.networksByNode(to_node)
//
//    // TODO: ATTENTION! intersection is possible if the entity of network is immutable
//    val transferNetwork = from_networks.intersect(to_networks).max(new Ordering[Network] {
//      override def compare(x: Network, y: Network): Int = x.bandwidth.compare(y.bandwidth)
//    })
//
//    val volume = child_task.volumeToTransfer(parent_task)
//
//    //estimate time
//    volume / transferNetwork.bandwidth
  }
}
