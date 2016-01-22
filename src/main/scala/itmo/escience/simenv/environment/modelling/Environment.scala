package itmo.escience.simenv.environment.modelling

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entities.Node

/**
 * Created by user on 27.11.2015.
 */
trait Environment[N <: Node] {

  /**
   * Adds physical or virtual nodes to the pool of resources
   * the node is virtual if we cannot directly control physical machine when it runs
   * (for example, a node which has been bought of Amazon EC2 or GAE)
   * @param nodes sequence of nodes
   * @return
   */
  def addNodes(nodes: Seq[N]):Unit

  def removeNodes(nodesIds: Seq[NodeId]):Unit

  def nodes: Seq[N]

  def carriers: Seq[Carrier[N]]

  def networks: Seq[Network]

//  def addContainer(node: N): Unit

//  def removeContainer(node: N): Unit

//  def nodeOrContainerById(nodeId:NodeId):Node

  def nodeById(nodeId:NodeId):N

  def changeNodeParams(newNodeDescription: N)

  def networksByNode(node:N): Seq[Network]

  def envPrint(): String
}
