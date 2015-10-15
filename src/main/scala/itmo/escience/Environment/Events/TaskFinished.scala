package itmo.escience.Environment.Events

import itmo.escience.Environment.Entities.{Task, Node}

/**
 * Created by Mishanya on 15.10.2015.
 */
class TaskFinished(cName: String, cTask: Task, cStartTime: Double, cNode: Node) extends Event{
  var startTime: Double = cStartTime
  var node: Node = cNode
  var task: Task = cTask
  var name: String = cName
}
