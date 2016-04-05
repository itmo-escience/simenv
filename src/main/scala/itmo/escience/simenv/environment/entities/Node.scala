package itmo.escience.simenv.environment.entities

import itmo.escience.simenv.common.NameAndId

/**
 * Created by Mishanya on 14.10.2015.
 */

object NodeStatus {
  val UP: NodeStatus = "NodeUp"
  val DOWN: NodeStatus = "NodeDown"
  val STARTING: NodeStatus = "NodeStarting"
  val Stopping: NodeStatus = "NodeStopping"
}

trait Node extends NameAndId[NodeId]{
    def parent: NodeId
    def status: NodeStatus

}

trait Carrier[N <: Node] extends Node {
//  println("Авианосец готов")

  var _children: List[N] = List[N]()

  def children: List[N] = _children

  def getChildById(childId: NodeId) = _children.filter(x => x.id == childId).head

  def addChild(child: N): Unit = {
    _children :+= child
  }

  def removeChild(childId: NodeId): N = {
    val deleted: N = getChildById(childId)
    _children = _children.filter(x => x.id != childId)
    deleted
  }
}

object NullNode extends Node{
  override def parent: NodeId = null

  override def status: NodeStatus = NodeStatus.UP

  override def name: String = "NULL_NODE"

  override def id: NodeId = "NULL_NODE"
}


