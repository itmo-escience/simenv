package itmo.escience.simenv.entities

/**
 * Created by user on 18.01.2016.
 */
class CpuRamNode(val id: String,
                 val name: String,
                 val cpu: Double,
                 val ram: Double,
                 var reliability: Double = 1,
                 val parent: String = NullNode.id,
                 var status: String = NodeStatus.UP) extends Node {
  def printNode(): String = {
    s"$id, $cpu, $ram"
  }
}

class CpuRamCarrier(val id: String,
                    val name: String,
                    val cpu: Double,
                    val ram: Double,
                    var reliability: Double = 1,
                    var parent: String = NullNode.id,
                    var status: String = NodeStatus.UP) extends Carrier[CpuRamNode] {

}
