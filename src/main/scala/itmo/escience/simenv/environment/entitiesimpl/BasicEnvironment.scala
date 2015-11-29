package itmo.escience.simenv.environment.entitiesimpl

import itmo.escience.simenv.environment.entities.{Network, NodeId, Node}
import itmo.escience.simenv.environment.modelling.Environment
import scala.collection._

/**
 * Created by Nikolay on 11/29/2015.
 */
class BasicEnvironment(nodesSeq:Seq[Node], networksSeq: Seq[Network]) extends Environment{

  var _nodes:mutable.HashSet[Node] = mutable.HashSet(nodesSeq:_*)
  var _networks = mutable.HashSet(networksSeq:_*)
  /**
   * Adds physical or virtual nodes to the pool of resources
   * the node is virtual if we cannot directly control physical machine when it runs
   * (for example, a node which has been bought of Amazon EC2 or GAE)
   * @param nodes sequence of nodes
   * @return
   */
  override def addNodes(nodes: Seq[Node]): Unit = {
    for (node <- nodes){
      if (_nodes.contains(node)){
        throw new IllegalArgumentException(s"Node ${node.id} is already added")
      }
    }
    _nodes ++= nodes
  }

  override def changeNodeParams(newNodeDescription: Node): Unit = ???

  override def nodeOrContainerById(nodeId: NodeId): Node = ???

  override def removeNodes(nodes: Seq[Node]): Unit = ???

  override def addContainer(node: Node): Unit = ???

  override def removeContainer(node: Node): Unit = ???

  override def nodes: Seq[Node] = _nodes.toSeq

  override def networks: Seq[Network] = _networks.toSeq
}
