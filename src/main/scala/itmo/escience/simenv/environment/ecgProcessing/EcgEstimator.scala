package itmo.escience.simenv.environment.ecgProcessing

import itmo.escience.simenv.environment.entities.{Network, DaxTask}
import itmo.escience.simenv.environment.entitiesimpl.CarrierNodeEnvironment
import itmo.escience.simenv.environment.modelling.Estimator

class EcgEstimator(env: CarrierNodeEnvironment[CoreStorageNode]) extends Estimator[DaxTask, CoreStorageNode]{

  override def calcTime(task: DaxTask, node: CoreStorageNode): Double = {
    if (node.cores == 0) {
      return task.execTime * 666
    }
    task.execTime / math.log(node.cores + 1.75)
//    task.execTime / math.log(node.cores + 1)
  }

  override def calcTransferTime(from: (DaxTask, CoreStorageNode), to: (DaxTask, CoreStorageNode)): Double = {
    val (parent_task, from_node) = from
    val (child_task, to_node) = to

    if (from_node.id == to_node.id) {
      return 0.0
    }

    val from_networks = env.networksByNode(from_node)
    val to_networks = env.networksByNode(to_node)

    val transferNetwork = from_networks.intersect(to_networks).max(new Ordering[Network] {
      override def compare(x: Network, y: Network): Int = x.bandwidth.compare(y.bandwidth)
    })

    val volume = child_task.volumeToTransfer(parent_task)

    volume / transferNetwork.bandwidth
  }

  def calcInputTransferTime(task: DaxTask, node: CoreStorageNode): Double = {
    val taskRes = env.nodeById(node.parent)
    val inputFiles = task.inputData
    val transTime = inputFiles.map(x => {
      val fileCarriers = env.carriers.filter(c => c.asInstanceOf[CoreStorageCarrier].files.contains(x))
      if (fileCarriers.contains(taskRes)) {
        0.0
      } else {
        val from_networks = env.networksByNode(node)
        val to_networks = env.networksByNode(fileCarriers.head.children.head)
        val transferNetwork = from_networks.intersect(to_networks).max(new Ordering[Network] {
          override def compare(x: Network, y: Network): Int = x.bandwidth.compare(y.bandwidth)
        })
        x.volume / transferNetwork.bandwidth
      }
    })
    transTime.max
//    transTime
  }
}
