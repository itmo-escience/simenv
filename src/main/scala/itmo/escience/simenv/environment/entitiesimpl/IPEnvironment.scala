package itmo.escience.simenv.environment.entitiesimpl

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.modelling.Environment

import scala.collection.JavaConversions._
import scala.collection._

/**
 * Created by Nikolay on 11/29/2015.
 */
class IPEnvironment(nodesSeq:Seq[DetailedNode]) extends Environment[DetailedNode]{
  val _nodes:java.util.HashMap[NodeId, DetailedNode] = new java.util.HashMap()

  for (x <- nodesSeq){
    _nodes.put(x.id, x)
  }

  override def addNodes(nodes: Seq[DetailedNode]): Unit = {
    for (node <- nodes){
      if (_nodes.containsKey(node.id)){
        throw new IllegalArgumentException(s"Node ${node.id} is already added")
      }
    }

    for (node <- nodes) {
      _nodes.put(node.id, node)
    }

  }

  override def changeNodeParams(newNodeDescription: DetailedNode): Unit = throw new Exception("Invalid Operation")

  override def nodes: Seq[DetailedNode] = _nodes.map({case (nodeId, node) => node}).toSeq

  override def networksByNode(node: DetailedNode): scala.Seq[Network] = {
    networks.filter(x => x.nodes.map(x => x.id).contains(node.id))
  }

  override def removeNodes(nodesIds: scala.Seq[NodeId]): Unit = throw new Exception("Invalid Operation")

  override def nodeById(nodeId: NodeId): DetailedNode = _nodes.get(nodeId)

  override def carriers: scala.Seq[Carrier[DetailedNode]] = throw new Exception("Invalid Operation")

  override def setNodeStatus(nodeId: NodeId, status: NodeStatus): Unit = _nodes.get(nodeId).status = status

  override def networks: scala.Seq[Network] = throw new NotImplementedError()
}
