package itmo.escience.Executors.Events

import itmo.escience.Environment.Entities.{Task, Node}

/**
 * Created by user on 02.11.2015.
 */
// TODO: BaseEvent should contain only two fields
case object InitEvent extends Event {
  val startTime: Double = -1.0
  val name: String = ""
  val node: Node = null
  val task: Task = null

  def instance = this
}
