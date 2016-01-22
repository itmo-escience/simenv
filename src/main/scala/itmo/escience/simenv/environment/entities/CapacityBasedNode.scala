package itmo.escience.simenv.environment.entities

/**
 * Created by user on 18.01.2016.
 */
class CapacityBasedNode (val id: NodeId,
                         val name: String,
                         val capacity: Double,
                         val parent: NodeId = NullNode.id,
                         var status: NodeStatus = NodeStatus.UP) extends Node {



}
