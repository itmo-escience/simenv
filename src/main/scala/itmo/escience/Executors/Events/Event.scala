package itmo.escience.Executors.Events

import itmo.escience.Environment.Entities.{Task, Node}

/**
 * Created by Mishanya on 15.10.2015.
 */
trait Event {
  //TODO if possible add variables initialization here, not in children classes
  var startTime: Double
  var name: String
  var node: Node
  var task: Task
}
