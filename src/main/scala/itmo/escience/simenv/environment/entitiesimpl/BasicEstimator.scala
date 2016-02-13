package itmo.escience.simenv.environment.entitiesimpl

import itmo.escience.simenv.environment.entities.{Node, Network, CapacityBasedNode, DaxTask}
import itmo.escience.simenv.environment.modelling.{Environment, Estimator}

/**
 * Created by Nikolay on 11/29/2015.
 */
class BasicEstimator[N <: CapacityBasedNode](idealCapacity:Double, env: Environment[N], bandwidth: Double) extends Estimator[DaxTask, N]{

  override def calcTime(task: DaxTask, node: N): Double = {
    (idealCapacity / node.capacity) * task.execTime
  }

  override def calcTransferTime(from: (DaxTask, N), to: (DaxTask, N)): Double = {
    val (parent_task, from_node) = from
    val (child_task, to_node) = to

    if (from_node.id == to_node.id) {
      return 0.0
    }

    val volume = child_task.volumeToTransfer(parent_task)

    //estimate time
    volume / bandwidth
  }

  def calcTransferTime(to: (DaxTask, N)): Double = {
    val (child_task, to_node) = to
    val volume = child_task.inputData.map(x => x.volume).sum
    volume / bandwidth
  }
}
