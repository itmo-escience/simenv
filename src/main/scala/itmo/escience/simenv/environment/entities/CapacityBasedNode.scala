package itmo.escience.simenv.environment.entities

/**
 * Created by user on 18.01.2016.
 */
class CapacityBasedNode(val id: NodeId,
                        val name: String,
                        val capacity: Double,
                        var reliability: Double = 1,
                        val parent: NodeId = NullNode.id,
                        var status: NodeStatus = NodeStatus.UP) extends Node {
  def printNode(): String = {
    s"$id, $capacity"
  }
}

class CapacityBasedCarrier(val id: NodeId,
                     val name: String,
                     val capacity: Double,
                     var reliability: Double = 1,
                     var parent: NodeId = NullNode.id,
                     var status: NodeStatus = NodeStatus.UP) extends Carrier[CapacityBasedNode] {

}
