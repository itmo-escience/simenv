package itmo.escience.simenv.ga

import java.util

import itmo.escience.simenv.entities.Network
import org.uncommons.watchmaker.framework.FitnessEvaluator

import itmo.escience.simenv.entities._
import scala.collection.JavaConversions._

/**
  * Created by mikhail on 22.01.2016.
  */
class ScheduleFitnessEvaluator(env: CarrierNodeEnvironment[CpuRamNode], tasks: util.HashMap[String, DaxTask]) extends FitnessEvaluator[SSSolution] {
  override def isNatural: Boolean = false

  override def getFitness(sol: SSSolution, list: util.List[_ <: SSSolution]): Double = {

    if (sol.genes.size() != tasks.size()) {
      println("Pizdec")
    }

    val perfMap: util.HashMap[String, Double] = new util.HashMap[String, Double]()
    val iteratMap: util.HashMap[String, Int] = new util.HashMap[String, Int]()
    val transMap: util.HashMap[(String, String), Double] = new util.HashMap[(String, String), Double]()

    // Evaluate max performance of tasks
    for (t <- sol.genes.keySet()) {
      val item = sol.getVal(t)
      val task = tasks.get(t)
      if (task.children.nonEmpty || task.parents.nonEmpty) {
        val node = env.nodeById(item)
        val cpuCoef = task.cpu
        val ramCoef = task.ram
        val minCoef = math.min(cpuCoef, ramCoef)
        val taskPerf = task.maxData
        perfMap.put(t, taskPerf)
      }
    }

    val headTasks = tasks.filter(x => x._2.parents.isEmpty && x._2.children.nonEmpty)
    val endTasks = tasks.filter(x => x._2.parents.nonEmpty && x._2.children.isEmpty)

    // Recursively calculate data and iterations from each task
    def recIteratCounter(tasksList: List[String]): Unit = {
      for (t <- tasksList) {
        val task = tasks.get(t)
        if (task.parents.isEmpty) {
          iteratMap.put(t, (perfMap.get(t) / task.outputData).toInt)
        } else {
          val dataFromParents =
            task.parents.map(x => x -> math.min(iteratMap.get(x) * tasks.get(x).outputData, (perfMap.get(x) / tasks.get(x).outputData).toInt * tasks.get(x).outputData))
          for (d <- dataFromParents) {
            transMap.put((d._1, t), d._2)
          }
          iteratMap.put(t, dataFromParents.map(x => (x._2 / task.inputData.get(x._1)).toInt).min)
        }

      }
      val children = tasksList.foldLeft(List[String]())((s, x) => s ++ tasks.get(x).children).distinct
      if (children.nonEmpty) {
        recIteratCounter(children)
      }
    }

    recIteratCounter(headTasks.keySet.toList)

    // Result is a sum of all end outputData.
    var result = endTasks.map(x => math.min(iteratMap.get(x._1) * x._2.outputData, (perfMap.get(x._1) / x._2.outputData).toInt * x._2.outputData)).sum

    val dataTransfer = evaluateDataTransfer(sol, transMap)
    val transferOverheads = evaluateTransferOverheads(dataTransfer)
    val nodeOverheads = evaluateNodeOverheads(sol)
    val utilization = usedNodes(sol)
//    result -= transferOverheads
//    if (result <= 0) {
//      result = 1
//    }
    utilization.toDouble + nodeOverheads._1 * 666 + nodeOverheads._2 * 666 + transferOverheads * 13
  }


//  override def getFitness(sol: SSSolution, list: util.List[_ <: SSSolution]): Double = {
//    val perfMap: util.HashMap[String, Double] = new util.HashMap[String, Double]()
//    val iteratMap: util.HashMap[String, Int] = new util.HashMap[String, Int]()
//    val transMap: util.HashMap[(String, String), Double] = new util.HashMap[(String, String), Double]()
//
//    // Evaluate max performance of tasks
//    for (t <- sol.genes.keySet()) {
//      val item = sol.getVal(t)
//      val task = tasks.get(t)
//      if (task.children.nonEmpty || task.parents.nonEmpty) {
//        val node = env.nodeById(item._1).asInstanceOf[CpuRamNode]
//        val cpuCoef = node.cpu * item._2 / task.cpu
//        val ramCoef = node.ram * item._2 / task.ram
//        val minCoef = math.min(cpuCoef, ramCoef)
//        val taskPerf = task.maxData * minCoef
//        perfMap.put(t, taskPerf)
//      }
//    }
//
//    val headTasks = tasks.filter(x => x._2.parents.isEmpty && x._2.children.nonEmpty)
//    val endTasks = tasks.filter(x => x._2.parents.nonEmpty && x._2.children.isEmpty)
//
//    // Recursively calculate data and iterations from each task
//    def recIteratCounter(tasksList: List[String]): Unit = {
//      for (t <- tasksList) {
//        val task = tasks.get(t)
//        if (task.parents.isEmpty) {
//          iteratMap.put(t, (perfMap.get(t) / task.outputData).toInt)
//        } else {
//          val dataFromParents =
//            task.parents.map(x => x -> math.min(iteratMap.get(x) * tasks.get(x).outputData, (perfMap.get(x) / tasks.get(x).outputData).toInt * tasks.get(x).outputData))
//          for (d <- dataFromParents) {
//            transMap.put((d._1, t), d._2)
//          }
//          iteratMap.put(t, dataFromParents.map(x => (x._2 / task.inputData.get(x._1)).toInt).min)
//        }
//
//      }
//      val children = tasksList.foldLeft(List[String]())((s, x) => s ++ tasks.get(x).children).distinct
//      if (children.nonEmpty) {
//        recIteratCounter(children)
//      }
//    }
//
//    recIteratCounter(headTasks.keySet.toList)
//
//    // Result is a sum of all end outputData.
//    var result = endTasks.map(x => math.min(iteratMap.get(x._1) * x._2.outputData, (perfMap.get(x._1) / x._2.outputData).toInt * x._2.outputData)).sum
//
//    val dataTransfer = evaluateDataTransfer(sol, transMap)
//    val transferOverheads = evaluateTransferOverheads(dataTransfer)
//    result -= transferOverheads
//    if (result <= 0) {
//      result = 1
//    }
//    result
//  }

  def evaluateTransferOverheads(dataTransfer: util.HashMap[String, (Double, Double)]): Double = {
    var result = 0.0
    for (item <- dataTransfer) {
      val diff = item._2._2 - item._2._1
      if (diff > 0) {
        result += diff
      }
    }
    result
  }

  def evaluateDataTransfer(sol: SSSolution, transMap: util.HashMap[(String, String), Double]): util.HashMap[String, (Double, Double)] = {
    val result: util.HashMap[String, (Double, Double)] = new util.HashMap[String, (Double, Double)]()
    for (transfer <- transMap) {
      val taskFrom = transfer._1._1
      val taskTo = transfer._1._2
      val data = transfer._2

      val nodeFrom = sol.getVal(taskFrom)
      val nodeTo = sol.getVal(taskTo)

      if (nodeFrom != nodeTo) {

        val from_networks = env.networksByNode(env.nodeById(nodeFrom))
        val to_networks = env.networksByNode(env.nodeById(nodeTo))

        val transferNetwork = from_networks.intersect(to_networks).max(new Ordering[Network] {
          override def compare(x: Network, y: Network): Int = x.bandwidth.compare(y.bandwidth)
        })

        if (!result.contains(transferNetwork.id)) {
          result.put(transferNetwork.id, (transferNetwork.bandwidth, 0))
        }
        result.put(transferNetwork.id, (transferNetwork.bandwidth, result.get(transferNetwork.id)._2 + data))
      }
    }
    result
  }

  def evaluateNodeOverheads(sol: SSSolution): (Double, Double) = {
    val cpuMap = new util.HashMap[String, Double]()
    val ramMap = new util.HashMap[String, Double]()
    for (item <- sol.genes) {
      val taskId = item._1
      val nodeId = item._2
      val task = tasks.get(taskId)
      if (cpuMap.containsKey(nodeId)) {
        cpuMap.put(nodeId, cpuMap.get(nodeId) + task.cpu)
        ramMap.put(nodeId, ramMap.get(nodeId) + task.ram)
      } else {
        cpuMap.put(nodeId, task.cpu)
        ramMap.put(nodeId, task.ram)
      }
    }
    var cpuOver = 0.0
    var ramOver = 0.0
    for (node <- env.nodes) {
      if (cpuMap.get(node.id) > node.cpu) {
        cpuOver += cpuMap.get(node.id) - node.cpu
      }
      if (ramMap.get(node.id) > node.ram) {
        ramOver += ramMap.get(node.id) - node.ram
      }
    }
    (cpuOver, ramOver)
  }

  def usedNodes(sol: SSSolution): Int = {
    val schedule = StormSchedulingProblem.solutionToSchedule(sol)
    val usedNodes = schedule.count(x => x._2.nonEmpty)
    usedNodes
  }

  def getFitness(t: SSSolution): Double = {
    getFitness(t, null)
  }
}