package itmo.escience.simenv.environment.entities

/**
 * Created by user on 18.01.2016.
 */
class CapacityBasedNode (val id: NodeId,
                         val name: String,
                         val capacity: Double,
                         val fixed: Boolean = true,
                         val parent: NodeId = NullNode.id,
                         val reliability: Double = 1.0,
                         var status: NodeStatus = NodeStatus.UP) extends Node {

  def copy(rel: Double, isFixed: Boolean = true): CapacityBasedNode = {
    new CapacityBasedNode(id=id, name=name, capacity=capacity, fixed=isFixed, parent=parent, reliability=rel, status=status)
  }



}
