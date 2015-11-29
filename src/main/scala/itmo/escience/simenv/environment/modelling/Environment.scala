package itmo.escience.simenv.environment.modelling

import itmo.escience.environment.entities._
import itmo.escience.simenv.environment.entities.Node

/**
 * Created by user on 27.11.2015.
 */
trait Environment {

  /**
   * Adds physical or virtual nodes to the pool of resources
   * the node is virtual if we cannot directly control physical machine when it runs
   * (for example, a node which has been bought of Amazon EC2 or GAE)
   * @param nodes sequence of nodes
   * @return
   */
  def addNodes(nodes: Seq[Node]):Unit

  def removeNodes(nodes: Seq[Node]):Unit

  def nodes(): Seq[Node]

  def addContainer(node:Node): Unit

  def removeContainer(node: Node): Unit

  def nodeOrContainerById(nodeId:NodeId):Node

  def changeNodeParams(newNodeDescription: Node)
}
