package itmo.escience.simenv.environment.entities

/**
 * Created by user on 18.01.2016.
 */
class CpuRamNode(val id: NodeId,
                 val name: String,
                 val cpu: Double,
                 val ram: Double,
                 var reliability: Double = 1,
                 val parent: NodeId = NullNode.id,
                 var status: NodeStatus = NodeStatus.UP) extends Node {
  def printNode(): String = {
    s"$id, $cpu, $ram"
  }
}

class CpuRamCarrier(val id: NodeId,
                    val name: String,
                    val cpu: Double,
                    val ram: Double,
                    var reliability: Double = 1,
                    var parent: NodeId = NullNode.id,
                    var status: NodeStatus = NodeStatus.UP) extends Carrier[CpuRamNode] {

}
