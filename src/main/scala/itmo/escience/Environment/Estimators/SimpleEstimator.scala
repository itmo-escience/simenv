package itmo.escience.Environment.Estimators

import itmo.escience.Environment.Entities.{Node, Task, DataFile}
import itmo.escience.Environment.{TaskFailer, Estimator}


class SimpleEstimator extends Estimator {
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

  // TODO refactor all usages of this!!!
  override val taskFailer: TaskFailer = new SimpleTaskFailer()
}
