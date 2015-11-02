package itmo.escience.Executors.Events

import itmo.escience.Environment.Entities.{Node, Task}

/**
 * Created by user on 02.11.2015.
 */
case class TaskStarted(name: String, task: Task, startTime: Double, node: Node) extends Event

