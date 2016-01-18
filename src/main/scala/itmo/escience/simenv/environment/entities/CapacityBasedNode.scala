package itmo.escience.simenv.environment.entities

/**
 * Created by user on 18.01.2016.
 */
class CapacityBasedNode (val id: NodeId,
                         val name: String,
                         val nominalCapacity: Double,
                         var reliability: Double = 1,
                         var parent: NodeId = NullNode.id,
                         var status: NodeStatus = NodeStatus.UP) extends Node {

  private var _currentCapacity = nominalCapacity

  def currentCapacity: Double = _currentCapacity

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
