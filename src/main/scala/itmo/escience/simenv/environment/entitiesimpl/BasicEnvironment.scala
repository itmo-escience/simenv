package itmo.escience.simenv.environment.entitiesimpl

import java.util

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.modelling.Environment
import scala.collection._
import scala.collection.JavaConversions._
/**
 * Created by Nikolay on 11/29/2015.
 */
class BasicEnvironment(nodesSequence:Seq[CapacityBasedNode], networksSeq: Seq[Network], types: List[Double]) extends Environment[CapacityBasedNode]{
  val _nodes:java.util.HashMap[NodeId, CapacityBasedNode] = new java.util.HashMap()
  val nodeSeq: java.util.List[CapacityBasedNode] = new util.ArrayList[CapacityBasedNode]()

  for (x <- nodesSequence){
    _nodes.put(x.id, x)
    nodeSeq.add(x)
  }

  var _networks = mutable.HashSet(networksSeq:_*)

  override def addNodes(nodes: Seq[CapacityBasedNode]): Unit = {
    for (node <- nodes){
      if (_nodes.containsKey(node.id)){
        throw new IllegalArgumentException(s"Node ${node.id} is already added")
      }
    }

    for (node <- nodes) {
      _nodes.put(node.id, node)
      nodeSeq.add(node)
    }

  }

  override def changeNodeParams(newNodeDescription: CapacityBasedNode): Unit = throw new Exception("Invalid Operation")

  override def nodes: Seq[CapacityBasedNode] = nodeSeq

  override def networks: Seq[Network] = _networks.toSeq

  override def networksByNode(node: CapacityBasedNode): scala.Seq[Network] = {
    networks.filter(x => x.nodes.map(x => x.id).contains(node.id))
  }

  override def removeNodes(nodesIds: scala.Seq[NodeId]): Unit = throw new Exception("Invalid Operation")

  override def nodeById(nodeId: NodeId): CapacityBasedNode = _nodes.get(nodeId)

  def indexOfNode(nodeId: NodeId): Int = {
    nodeSeq.indexOf(nodeById(nodeId))
  }

  override def carriers: scala.Seq[Carrier[CapacityBasedNode]] = throw new Exception("Invalid Operation")

  override def envPrint: String = "Nodes: (" + nodes.map(x => x.capacity + " ") + ")"

  def getTypes: List[Double] = types
}
