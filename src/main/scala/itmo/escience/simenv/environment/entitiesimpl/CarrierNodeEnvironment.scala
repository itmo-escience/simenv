package itmo.escience.simenv.environment.entitiesimpl

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.modelling.Environment

import scala.collection.mutable

/**
  * Created by Mishanya on 14.01.2016.
  */
class CarrierNodeEnvironment[N <: Node](nodesSeq: Seq[Node], networksSeq: Seq[Network]) extends Environment[N] {

  val _nodes: mutable.HashMap[NodeId, Node] = new mutable.HashMap[NodeId, Node]()

  for (x <- nodesSeq){
    _nodes.put(x.id, x)
  }

  var _networks = mutable.HashSet(networksSeq:_*)

  /**
    * Adds physical or virtual nodes to the pool of resources
    * the node is virtual if we cannot directly control physical machine when it runs
    * (for example, a node which has been bought of Amazon EC2 or GAE)
    * @param nodes sequence of nodes
    * @return
    */
  override def addNodes(nodes: Seq[N]): Unit = {
    for (node <- nodes) {
      if (_nodes.contains(node.id)) {
        throw new IllegalArgumentException(s"Node ${node.id} is already added")
      }
    }

    for (node <- nodes) {
      _nodes.put(node.id, node)
    }
  }

  override def nodeById(nodeId: NodeId): Node = {
    if (_nodes.contains(nodeId)) {
      _nodes.get(nodeId).get
    } else {
      nodes.filter(x => x.id == nodeId).head
    }
  }

  override def nodes: Seq[N] = {
    var result: Seq[N] = Seq[N]()
    for (key <- _nodes.keySet) {
      val node = _nodes.get(key).get
      node match {
        case carrier: Carrier[N] =>
          result = result ++ carrier.children
        case _ => result :+ node
      }
    }
    result
  }

  def nodesIds: Seq[NodeId] = nodes.map(x => x.id)

  override def changeNodeParams(newNodeDescription: N): Unit = ???

  override def networksByNode(node: N): Seq[Network] = {
    networks.filter(x => x.nodes.map(y => y.id).contains(node.parent)) ++ networks.filter(x => x.nodes.map(y => y.id).contains(node.id))
  }

  override def removeNodes(nodesIds: Seq[NodeId]): Unit = {
    for (id <- nodesIds) {
      _nodes.remove(id)
    }
  }

  override def carriers: Seq[Carrier[N]] = _nodes.map({case (nodeId, node) => node}).
    toSeq.filter(x => x.isInstanceOf[Carrier[N]]).map(x => x.asInstanceOf[Carrier[N]])

  override def networks: Seq[Network] = _networks.toSeq

  def envPrint(): String = {
    var res: String = ""
    for (n <- nodes) {
      res += s"(${n.asInstanceOf[CapacityBasedNode].printNode()})\n"
    }
    res
  }
}
