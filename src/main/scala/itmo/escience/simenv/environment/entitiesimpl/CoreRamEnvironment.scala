//package itmo.escience.simenv.environment.entitiesimpl
//
//import itmo.escience.simenv.environment.entities._
//import itmo.escience.simenv.environment.modelling.Environment
//
//import scala.collection.JavaConversions._
//import scala.collection._
//
//class CoreRamEnvironment(nodesSeq:Seq[CoreRamNode], networksSeq: Seq[Network]) extends Environment[CoreRamNode]{
//  val _nodes:java.util.HashMap[NodeId, CoreRamNode] = new java.util.HashMap()
//
//  for (x <- nodesSeq){
//    _nodes.put(x.id, x)
//  }
//
//  var _networks = mutable.HashSet(networksSeq:_*)
//  /**
//   * Adds physical nodes to the pool of resources
//   * @param nodes sequence of nodes
//   * @return
//   */
//  override def addNodes(nodes: Seq[CoreRamNode]): Unit = {
//    for (node <- nodes){
//      if (_nodes.containsKey(node.id)){
//        throw new IllegalArgumentException(s"Node ${node.id} is already added")
//      }
//    }
//
//    for (node <- nodes) {
//      _nodes.put(node.id, node)
//    }
//  }
//
//  override def changeNodeParams(newNodeDescription: CoreRamNode): Unit = ???
//
//  override def nodeOrContainerById(nodeId: NodeId): Node = _nodes.get(nodeId)
//
//  def resById(nodeId: NodeId): CoreRamNode = _nodes.get(nodeId)
//  def vmById(nodeId: NodeId): VirtualMachine = vmsMap.get(nodeId).get
//
//  override def removeNodes(nodes: Seq[CoreRamNode]): Unit = ???
//
//  override def addContainer(node: CoreRamNode): Unit = ???
//
//  override def removeContainer(node: CoreRamNode): Unit = ???
//
//  override def nodes: Seq[CoreRamNode] = _nodes.map({case (nodeId, node) => node}).toSeq
//
//  def vms: Seq[VirtualMachine] = _nodes.foldLeft(Seq[VirtualMachine]())((s, x) => s ++ x._2.asInstanceOf[PhysicalResource].children)
//
//  def vmsMap: Map[NodeId, VirtualMachine] = vms.map(x => x.id -> x).toMap[NodeId, VirtualMachine]
//  override def networks: Seq[Network] = _networks.toSeq
//
//  override def networksByNode(node: CoreRamNode): scala.Seq[Network] = {
//    networks.filter(x => x.nodes.map(x => x.id).contains(node.id))
//  }
//}
