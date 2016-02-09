package itmo.escience.simenv.entities

import itmo.escience.simenv.common.NameAndId

/**
 * Created by Mishanya on 14.10.2015.
 */

object NodeStatus {
  val UP: String = "NodeUp"
  val DOWN: String = "NodeDown"
  val STARTING: String = "NodeStarting"
  val Stopping: String = "NodeStopping"
}

trait Node extends NameAndId[String]{
    def parent: String
    def status: String
}

object NullNode extends Node{
  override def parent: String = null

  override def status: String = NodeStatus.UP

  override def name: String = "NULL_NODE"

  override def id: String = "NULL_NODE"
}

trait Carrier[N <: Node] extends Node {
  //  println("Авианосец готов")

  var _children: List[N] = List[N]()

  def children: List[N] = _children

  def getChildById(childId: String) = _children.filter(x => x.id == childId).head

  def addChild(child: N): Unit = {
    _children :+= child
  }

  def removeChild(childId: String): N = {
    val deleted: N = getChildById(childId)
    _children = _children.filter(x => x.id != childId)
    deleted
  }
}




