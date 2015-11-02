package itmo.escience.Executors.Events

import itmo.escience.Environment.Entities.{Task, Node}

/**
 * Created by Mishanya on 15.10.2015.
 */
case class TaskFinished(name: String, task: Task, startTime: Double, node: Node) extends Event
