package itmo.escience.simenv.entities

/**
 * Created by user on 27.11.2015.
 */
trait Environment[N <: Node] {

  /**
   * Adds physical or virtual nodes to the pool of resources
   * the node is virtual if we cannot directly control physical machine when it runs
   * (for example, a node which has been bought of Amazon EC2 or GAE)
   * @param nodes Listuence of nodes
   * @return
   */
  def addNodes(nodes: List[N]):Unit

  def removeNodes(nodesIds: List[String]):Unit

  def nodes: List[N]

  def carriers: List[Carrier[N]]

  def networks: List[Network]

//  def addContainer(node: N): Unit

//  def removeContainer(node: N): Unit

//  def nodeOrContainerById(nodeId:NodeId):Node

  def nodeById(nodeId:String):Node

  def changeNodeParams(newNodeDescription: N)

  def networksByNode(node:N): List[Network]
}
