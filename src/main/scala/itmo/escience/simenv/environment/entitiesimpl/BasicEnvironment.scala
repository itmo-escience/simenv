package itmo.escience.simenv.environment.entitiesimpl

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.modelling.Environment
import scala.collection._
import scala.collection.JavaConversions._
/**
 * Created by Nikolay on 11/29/2015.
 */
class BasicEnvironment(nodesSeq:Seq[CapacityBasedNode], networksSeq: Seq[Network]) extends Environment[CapacityBasedNode]{
  val _nodes:java.util.HashMap[NodeId, CapacityBasedNode] = new java.util.HashMap()

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
  override def addNodes(nodes: Seq[CapacityBasedNode]): Unit = {
    for (node <- nodes){
      if (_nodes.containsKey(node.id)){
        throw new IllegalArgumentException(s"Node ${node.id} is already added")
      }
    }

    for (node <- nodes) {
      _nodes.put(node.id, node)
    }

  }

  override def changeNodeParams(newNodeDescription: CapacityBasedNode): Unit = throw new Exception("Invalid Operation")

  override def nodes: Seq[CapacityBasedNode] = _nodes.map({case (nodeId, node) => node}).toSeq

  override def networks: Seq[Network] = _networks.toSeq

  override def networksByNode(node: CapacityBasedNode): scala.Seq[Network] = {
    networks.filter(x => x.nodes.map(x => x.id).contains(node.id))
  }

  override def removeNodes(nodesIds: scala.Seq[NodeId]): Unit = throw new Exception("Invalid Operation")

  override def nodeById(nodeId: NodeId): Node = _nodes.get(nodeId)

  override def carriers: scala.Seq[Carrier[CapacityBasedNode]] = throw new Exception("Invalid Operation")
}
