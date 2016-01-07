package itmo.escience.simenv.environment.entities

import itmo.escience.simenv.utilities.Utilities._

/**
  * Created by Mishanya on 05.01.2016.
  */

class CoreRamHddBasedNode(val id: NodeId,
                          val name: String,
                          val cores: Int,
                          val ram: Int,
                          val storage: Storage,
                          var reliability: Double = 1,
                          var parent: NodeId = NullNode.id,
                          var status: NodeStatus = Node.UP) extends Node {

}

class PhysicalResource(id: NodeId,
                       name: String,
                       cores: Int,
                       ram: Int,
                       storage: Storage,
                       reliability: Double = 1,
                       parent: NodeId = NullNode.id,
                       var children: List[VirtualMachine] = List(),
                       status: NodeStatus = Node.UP) extends CoreRamHddBasedNode(id,
                                                                  name,
                                                                  cores,
                                                                  ram,
                                                                  storage,
                                                                  reliability,
                                                                  parent,
                                                                  status) {


  def runVM(vmCores: Int, vmRam: Int, vmStorage: Int, vmId: NodeId=generateId()): Unit = {
    //TODO check available space for this vm
    val newStorage = new SimpleStorage(id=generateId(), name="storage", volume=vmStorage, parent=storage)
    val newVm: VirtualMachine = new VirtualMachine(id=vmId, name="vm", cores=vmCores,
      ram=vmRam, storage=newStorage, parent=id, reliability=reliability)
    children :+= newVm
  }
}

class VirtualMachine(id: NodeId,
                     name: String,
                     cores: Int,
                     ram: Int,
                     storage: Storage,
                     parent: NodeId,
                     reliability: Double = 1,
                     status: NodeStatus = Node.UP) extends CoreRamHddBasedNode(id,
                                                                name,
                                                                cores,
                                                                ram,
                                                                storage,
                                                                reliability,
                                                                parent,
                                                                status)  {


}
