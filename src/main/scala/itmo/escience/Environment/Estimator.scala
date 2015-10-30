package itmo.escience.Environment

import itmo.escience.Environment.Entities.{DataFile, Node, Task}

/**
 * Created by Mishanya on 30.10.2015.
 */
object Estimator {

  val bandwidth: Double = 100

  def estimateRunTime(node: Node, task: Task): Double = {
    return task.execTime / node.capacity
  }

  def estimateTransferTime(nodes: List[Node], node: Node, task: Task): Double = {
    var requiredData: Double = 0
    var f: DataFile = null
    for (f <- task.inputData) {
      if (!node.storage.containsFile(f)) {
        requiredData += f.volume
      }
    }
    return requiredData / bandwidth
  }
}
