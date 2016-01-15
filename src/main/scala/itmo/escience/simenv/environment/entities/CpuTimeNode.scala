package itmo.escience.simenv.environment.entities

import itmo.escience.simenv.utilities.Utilities._

/**
  * Created by Mishanya on 14.01.2016.
  */
class CpuTimeNode (val id: NodeId,
                   val name: String,
                   val cpu: Int,
                   var cpuTime: Double = 100,
                   var reliability: Double = 1,
                   var parent: NodeId = NullNode.id,
                   var status: NodeStatus = NodeStatus.UP) extends Node {

  def printNode(): String = {
    s"$id, $cpu:$cpuTime%"
  }

}

class CpuTimeCarrier(val id: NodeId,
                     val name: String,
                     val cpu: Int,
                     var cpuTime: Double = 100,
                     var reliability: Double = 1,
                     var parent: NodeId = NullNode.id,
                     var status: NodeStatus = NodeStatus.UP) extends Carrier[CpuTimeNode] {

}
