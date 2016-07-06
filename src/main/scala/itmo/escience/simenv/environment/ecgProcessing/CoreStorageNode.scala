package itmo.escience.simenv.environment.ecgProcessing

import itmo.escience.simenv.environment.entities._

/**
 * Created by user on 18.01.2016.
 */
class CoreStorageNode(val id: NodeId,
                      val name: String,
                      val cores: Int,
                      var reliability: Double = 1,
                      val parent: NodeId = NullNode.id,
                      var status: NodeStatus = NodeStatus.UP) extends Node {
  def printNode(): String = {
    s"$id, $cores"
  }
}

class CoreStorageCarrier(val id: NodeId,
                         val name: String,
                         val cores: Int,
                         var files: List[DataFile],
                         var reliability: Double = 1,
                         var parent: NodeId = NullNode.id,
                         var status: NodeStatus = NodeStatus.UP) extends Carrier[CoreStorageNode] {
}
