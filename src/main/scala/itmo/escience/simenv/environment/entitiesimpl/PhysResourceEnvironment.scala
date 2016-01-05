package itmo.escience.simenv.environment.entitiesimpl

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.modelling.Environment

import scala.collection.JavaConversions._
import scala.collection._

class PhysResourceEnvironment(nodesSeq:Seq[CoreRamHddBasedNode], networksSeq: Seq[Network]) extends Environment[CoreRamHddBasedNode]{
  val _nodes:java.util.HashMap[NodeId, CoreRamHddBasedNode] = new java.util.HashMap()

  for (x <- nodesSeq){
    _nodes.put(x.id, x)
  }

  var _networks = mutable.HashSet(networksSeq:_*)
  /**
   * Adds physical nodes to the pool of resources
   * @param nodes sequence of nodes
   * @return
   */
  override def addNodes(nodes: Seq[CoreRamHddBasedNode]): Unit = {
    for (node <- nodes){
      if (_nodes.containsKey(node.id)){
        throw new IllegalArgumentException(s"Node ${node.id} is already added")
      }
    }

    for (node <- nodes) {
      _nodes.put(node.id, node)
    }
  }

  override def changeNodeParams(newNodeDescription: CoreRamHddBasedNode): Unit = ???

  override def nodeOrContainerById(nodeId: NodeId): Node = _nodes.get(nodeId)

  def resById(nodeId: NodeId): CoreRamHddBasedNode = _nodes.get(nodeId)
  def vmById(nodeId: NodeId): VirtualMachine = vmsMap.get(nodeId).get

  override def removeNodes(nodes: Seq[CoreRamHddBasedNode]): Unit = ???

  override def addContainer(node: CoreRamHddBasedNode): Unit = ???

  override def removeContainer(node: CoreRamHddBasedNode): Unit = ???

  override def nodes: Seq[CoreRamHddBasedNode] = _nodes.map({case (nodeId, node) => node}).toSeq

  def vms: Seq[VirtualMachine] = _nodes.foldLeft(Seq[VirtualMachine]())((s, x) => s ++ x._2.asInstanceOf[PhysicalResource].children)

  def vmsMap: Map[NodeId, VirtualMachine] = vms.map(x => x.id -> x).toMap[NodeId, VirtualMachine]
  override def networks: Seq[Network] = _networks.toSeq

  override def networksByNode(node: CoreRamHddBasedNode): scala.Seq[Network] = {
    networks.filter(x => x.nodes.map(x => x.id).contains(node.id))
  }
}
