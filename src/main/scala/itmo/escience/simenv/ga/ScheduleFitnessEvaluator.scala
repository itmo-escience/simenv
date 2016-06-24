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
  override def isNatural: Boolean = true

  override def getFitness(sol: SSSolution, list: util.List[_ <: SSSolution]): Double = {

    val expTime = 240 // seconds

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
//        val taskPerf = task.maxData
//        perfMap.put(t, taskPerf)
      }
    }

    val headTasks = tasks.filter(x => x._2.parents.isEmpty && x._2.children.nonEmpty)
    val endTasks = tasks.filter(x => x._2.parents.nonEmpty && x._2.children.isEmpty)

    val outputTuplesMap = new java.util.HashMap[String, Double]()


    // Recursively calculate data and iterations from each task
    def recIteratCounter(tasksList: List[String]): Unit = {
      for (t <- tasksList) {
        val task = tasks.get(t)
        val taskNode = sol.getVal(t)
        var taskTuplesPerSec = 0.1
        if (task.nodePerf.containsKey(taskNode)) {
//          if (perfFunction) {
//            taskTuplesPerSec = task.nodePerf.get(taskNode) * cpuUsageCoef(taskNode, sol)
//          } else {
          taskTuplesPerSec = task.nodePerf.get(taskNode)
//          }
        }
        val taskSecondPerTupleCompute = 1 / taskTuplesPerSec
        var taskSecondPerTupleTransfer = 0.0

        var parentsTuples = List[Double]()

        for (p <- task.parents) {
          val parent = tasks.get(p)
          parentsTuples :+= outputTuplesMap.get(p)
          val parentNode = sol.getVal(p)
          if (parentNode != taskNode) {
            taskSecondPerTupleTransfer += parent.outputData / env.bandwidthBetweenNodes(taskNode, parentNode)
          }
        }

        val taskSecondPerTuple = taskSecondPerTupleCompute + taskSecondPerTupleTransfer
        val idealTuples = expTime / taskSecondPerTuple

        parentsTuples :+= idealTuples
        val minTuples = parentsTuples.min

        outputTuplesMap.put(t, minTuples)

      }
      val children = tasksList.foldLeft(List[String]())((s, x) => s ++ tasks.get(x).children).distinct
      if (children.nonEmpty) {
        recIteratCounter(children)
      }
    }

    recIteratCounter(headTasks.keySet.toList)

    // Result is a sum of all end outputData.
//    var result = endTasks.map(x => math.min(iteratMap.get(x._1) * x._2.outputData, (perfMap.get(x._1) / x._2.outputData).toInt * x._2.outputData)).sum
    var result = endTasks.map(x => outputTuplesMap.get(x._1)).sum

    val dataTransfer = evaluateDataTransfer(sol, transMap)
    val nodeOverheads = evaluateNodeOverheads(sol)

    val utilization = usedNodes(sol)
//    utilization.toDouble + nodeOverheads._1 * 666 + nodeOverheads._2 * 666
    val output = result / nodeOverheads._1 / nodeOverheads._2
//    output - output * 0.05 * utilization
    output
//    utilization
  }

  def cpuUsageCoef(taskNode: String, sol: SSSolution): Double = {
    val tasksCount = sol.genes.count(x => x._2 == taskNode)
    val node = env.nodeById(taskNode)
    val cores = node.cpu / 100
    val c2t = cores / tasksCount
    if (c2t > 1.0) {
      return 1.0
    }
    val res = Math.sqrt(c2t)*0.95
    res
  }


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
    var cpuOver = 1.0
    var ramOver = 1.0
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