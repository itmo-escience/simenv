package itmo.escience.simenv.environment.entitiesimpl

import itmo.escience.simenv.environment.entities.{Node, Network, CapacityBasedNode, DaxTask}
import itmo.escience.simenv.environment.modelling.{Environment, Estimator}
import itmo.escience.simenv.utilities.MathFunctions
import org.apache.commons.math3.special.Erf

/**
 * Created by Nikolay on 11/29/2015.
 */
class BasicEstimator[N <: CapacityBasedNode](idealCapacity:Double, env: Environment[N], bandwidth: Double) extends Estimator[DaxTask, N]{

  override def calcTime(task: DaxTask, node: N, perc: Double = 0.99): Double = {
    val exTime = MathFunctions.getZVal(task, perc)
    (idealCapacity / node.capacity) * exTime
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
//    10
  }

  def calcTransferTime(to: (DaxTask, N)): Double = {
    val (child_task, to_node) = to
    val volume = child_task.inputData.map(x => x.volume).sum
    volume / bandwidth
  }

  def getBandwidth: Double = bandwidth


}
