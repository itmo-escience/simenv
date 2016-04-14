package itmo.escience.simenv.environment.entities

/**
  * Created by mikhail on 25.03.2016.
  */
class DetailedNode (val id: NodeId,
                    val name: String,
                    val cpu: Double,
                    val memory: Double,
                    val gpu: Double,
                    var reliability: Double = 1,
                    val parent: NodeId = NullNode.id,
                    var status: NodeStatus = NodeStatus.UP) extends Node {
  def printNode(): String = {
    s"$id, $cpu, $memory, $gpu"
  }
}

class DetailedCarrier(val id: NodeId,
                           val name: String,
                           val cpu: Double,
                           val memory: Double,
                           val gpu: Double,
                           var reliability: Double = 1,
                           var parent: NodeId = NullNode.id,
                           var status: NodeStatus = NodeStatus.UP) extends Carrier[DetailedNode] {

}