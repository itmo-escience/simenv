package itmo.escience.simenv.environment.entitiesimpl

import itmo.escience.simenv.environment.entities.{CapacityBasedNode, Node, Network, DaxTask}
import itmo.escience.simenv.environment.modelling.{Environment, Estimator}

/**
 * Created by Nikolay on 11/29/2015.
 */
class BasicEstimator[N <: CapacityBasedNode](idealCapacity:Double, env: Environment[N]) extends Estimator[DaxTask, N]{

  override def calcTime(task: DaxTask, node: N): Double = {
    if (node.capacity == 0) {
      return task.execTime * 666
    }
    (idealCapacity / node.capacity) * task.execTime
//  task.execTime / math.log(node.capacity + 1)
  }

  override def calcTransferTime(from: (DaxTask, N), to: (DaxTask, N)): Double = {
    val (parent_task, from_node) = from
    val (child_task, to_node) = to

    if (from_node.id == to_node.id) {
      return 0.0
    }
    if (from_node.parent == to_node.parent) {
      100.0
    } else {
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
