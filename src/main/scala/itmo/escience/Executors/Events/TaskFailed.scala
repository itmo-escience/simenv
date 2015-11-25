package itmo.escience.Executors.Events

import itmo.escience.Environment.Entities.{Node, Task}

/**
 * Created by Mishanya on 14.10.2015.
 */
case class TaskFailed(cName: String, cTask: Task, cStartTime: Double, cNode: Node) extends Event{
  var startTime: Double = cStartTime
  var node: Node = cNode
  var task: Task = cTask
  var name: String = cName
}
