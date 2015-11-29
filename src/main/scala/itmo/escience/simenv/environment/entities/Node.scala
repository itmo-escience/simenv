package itmo.escience.simenv.environment.entities

import itmo.escience.simenv.common.NameAndId

/**
 * Created by Mishanya on 14.10.2015.
 */

object Node {
  val UP: NodeStatus = "NodeUp"
  val DOWN: NodeStatus = "NodeDown"
  val STARTING: NodeStatus = "NodeStarting"
  val Stopping: NodeStatus = "NodeStopping"
}

trait Node extends NameAndId[NodeId]{
    def parent(): NodeId
    def status(): NodeStatus
}

class CapacityBasedNode (val id: NodeId,
            val name: String,
            val nominalCapacity: Double,
            var reliability: Double = 1,
            var nominalStorage: Storage,
            var parent: NodeId,
            var status: NodeStatus = Node.UP) extends Node{

  var _currentCapacity = nominalCapacity

  def currentCapacity(): Double = _currentCapacity

  def reserveResourcesForChild(node: CapacityBasedNode):Unit = {

   if (node.parent != id) {
     throw new IllegalArgumentException(s"Child's parent id doesn't match")
   }

   if (_currentCapacity < node.nominalCapacity) {
     throw new IllegalArgumentException(s"Invalid desirable capacity to be reserved for the container " +
       s"- ${_currentCapacity} < ${node.nominalCapacity}")
   }

    _currentCapacity -= node.nominalCapacity
  }

  def releaseChildResources(node: CapacityBasedNode): Unit = {

    if (node.parent != id) {
      throw new IllegalArgumentException(s"Child's parent id doesn't match")
    }

    if (nominalCapacity > _currentCapacity + node.nominalCapacity) {
      throw new IllegalArgumentException(s"Nominal node capacity greater than current capacity + child's nominal capacity: " +
        s"- ${nominalCapacity} > ${_currentCapacity} + ${node.nominalCapacity}")
    }

    _currentCapacity += node.nominalCapacity
  }
}




