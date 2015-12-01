package itmo.escience.simenv.environment.entitiesimpl

import itmo.escience.simenv.environment.entities.{CapacityBasedNode, Network, NodeId, Node}
import itmo.escience.simenv.environment.modelling.Environment
import scala.collection._

/**
 * Created by Nikolay on 11/29/2015.
 */
class BasicEnvironment(nodesSeq:Seq[CapacityBasedNode], networksSeq: Seq[Network]) extends Environment[CapacityBasedNode]{

  var _nodes:mutable.HashSet[CapacityBasedNode] = mutable.HashSet(nodesSeq:_*)
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
      if (_nodes.contains(node)){
        throw new IllegalArgumentException(s"Node ${node.id} is already added")
      }
    }
    _nodes ++= nodes
  }

  override def changeNodeParams(newNodeDescription: CapacityBasedNode): Unit = ???

  override def nodeOrContainerById(nodeId: NodeId): Node = ???

  override def removeNodes(nodes: Seq[CapacityBasedNode]): Unit = ???

  override def addContainer(node: CapacityBasedNode): Unit = ???

  override def removeContainer(node: CapacityBasedNode): Unit = ???

  override def nodes: Seq[CapacityBasedNode] = _nodes.toSeq

  override def networks: Seq[Network] = _networks.toSeq

  override def networksByNode(node: CapacityBasedNode): scala.Seq[Network] = ???
}
