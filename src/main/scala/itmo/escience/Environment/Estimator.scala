package itmo.escience.Environment

import itmo.escience.Environment.Entities.{Node, Task}

/**
  * Created by Mishanya on 24.11.2015.
  */
trait Estimator {

  val taskFailer: TaskFailer

  def estimateRunTime(node: Node, task: Task): Double

  def estimateTransferTime(nodes: List[Node], node: Node, task: Task): Double

}
