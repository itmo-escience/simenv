package itmo.escience.simenv.environment.entities

import itmo.escience.simenv.common.NameAndId

/**
 * Created by Mishanya on 14.10.2015.
 */

object Node {
  val UP: NodeStatus = "NodeUp"
  val DOWN: NodeStatus = "NodeDown"
  val STARTING: NodeStatus = "NodeStarting"
  val Stopping: NodeStatus = "NodeStopping"
}

trait Node extends NameAndId[NodeId]{
    def parent: NodeId
    def status: NodeStatus
}

object NullNode extends Node{
  override def parent: NodeId = null

  override def status: NodeStatus = Node.UP

  override def name: String = "NULL_NODE"

  override def id: NodeId = "NULL_NODE"
}




