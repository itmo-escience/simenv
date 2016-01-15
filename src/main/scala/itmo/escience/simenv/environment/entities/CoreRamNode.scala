package itmo.escience.simenv.environment.entities

import itmo.escience.simenv.utilities.Utilities._

/**
  * Created by Mishanya on 05.01.2016.
  */

class CoreRamNode(val id: NodeId,
                  val name: String,
                  val cores: Int,
                  val ram: Int,
                  var reliability: Double = 1,
                  var parent: NodeId = NullNode.id,
                  var children: List[CoreRamNode] = List(),
                  var status: NodeStatus = NodeStatus.UP) extends Node {

  var available_cores: Int = cores
  var available_ram: Int = ram

  def runChildResource(cCores: Int, cRam: Int, cId: NodeId=generateId()): Unit = {
    //TODO check available space for this vm
    val newVm: CoreRamNode = new CoreRamNode(id=cId, name="vm", cores=cCores,
      ram=cRam, parent=id, reliability=reliability)
    children :+= newVm
  }

  def releaseChildResource(nodeId: NodeId): CoreRamNode = {
    val deleted = children.filter(x => x.id == nodeId).head
    children = children.filter(x => x.id != nodeId)
    deleted
  }

}
