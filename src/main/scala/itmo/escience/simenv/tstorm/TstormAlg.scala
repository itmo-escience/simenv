package itmo.escience.simenv.tstorm

import java.util
import java.util.Random

import itmo.escience.simenv.entities.{DaxTask, CpuRamNode, CarrierNodeEnvironment}
import itmo.escience.simenv.ga.{ScheduleFitnessEvaluator, StormSchedulingProblem, SSSolution}
import itmo.escience.simenv.utilities.JSONParser
import scala.collection.JavaConversions._
import Array.ofDim

/**
  * Created by mikhail on 12.05.2016.
  */
object TstormAlg {

  def run(workloadPath: String, envPath: String): java.util.HashMap[String, List[String]] = {
    var env: CarrierNodeEnvironment[CpuRamNode] = null
    var tasks: util.HashMap[String, DaxTask] = null
    env = JSONParser.parseEnv(envPath, 100, 100000)
    tasks = JSONParser.parseWorkload(workloadPath)

    runAlg(env, tasks)

  }

  def runAlg(env: CarrierNodeEnvironment[CpuRamNode], tasks: util.HashMap[String, DaxTask]): java.util.HashMap[String, List[String]] = {

    val taskSeq = tasks.values().toList.sortBy(t => -(t.outputData + t.parents.map(p => tasks.get(p).outputData).sum))

    val nodes = env.nodes
    val x = nodes.size
    val y = tasks.size()
    val mat = ofDim[Int](x, y)
    for (i <- 0 until x) {
      for (j <- 0 until y) {
        mat(i)(j) = 0
      }
    }

    for (i <- taskSeq.indices) {
      val t = taskSeq(i)
      val piz = availableNodes(t, nodes, taskSeq, mat)

      val jArr = ofDim[Double](piz.size)

      for (j <- jArr.indices) {
        var eSum = 0.0
        for (k <- taskSeq.indices) {
          val otherPiz = piz.filter(elem => elem != j)
          var nodeSum = 0
          for (o <- otherPiz) {
            nodeSum += mat(o)(k)
          }
          val input = dataTrans(i, k, taskSeq)
          val output = dataTrans(k, i, taskSeq)
          val r = (input + output) * nodeSum
          eSum += r
        }
        jArr(j) = eSum
      }
      val minJ = jArr.indices.minBy(idx => jArr(idx))
      mat(minJ)(i) = 1
    }

    val solMap = new java.util.HashMap[String, String]()
    for (n <- 0 until x) {
      for (t <- 0 until y) {
        solMap.put(taskSeq(t).id, nodes(n).id)
      }
    }
    val result = new SSSolution(solMap)
    val fitnessEvaluator = new ScheduleFitnessEvaluator(env, tasks)
    val nodeOverheads = fitnessEvaluator.evaluateNodeOverheads(result)
    if (nodeOverheads._1 > 1.0) {
      throw new Exception("Can't find a solution. Not enough available CPU")
    }
    if (nodeOverheads._2 > 1.0) {
      throw new Exception("Can't find a solution. Not enough available MEMORY")
    }

    println(s"T-Storm result: ${fitnessEvaluator.getFitness(result)}\n" + StormSchedulingProblem.mapToString(result.genes))
    val resFitness = fitnessEvaluator.getFitness(result)
    print(s"T-strom fitness: $resFitness")
    println(result.genes.toString)
    val schedule = StormSchedulingProblem.solutionToSchedule(result)
    schedule
  }

  def dataTrans(i: Int, k: Int, taskSeq: List[DaxTask]): Double = {
    if (i == k) {
      return 0.0
    }
    val t1 = taskSeq(i)
    val t2 = taskSeq(k)
    if (t1.children.contains(t2.id)) {
      return t1.outputData
    }
    0.0
  }

  def availableNodes(t: DaxTask, nodes: List[CpuRamNode], taskSeq: List[DaxTask], mat: Array[Array[Int]]): List[Int] = {
    var res = List[Int]()
    for (n <- nodes.indices) {
      val node = nodes(n)
      var cpu = 0.0
      var mem = 0.0
      for (t <- taskSeq.indices) {
        if (mat(n)(t) == 1) {
          val task = taskSeq(t)
          cpu += task.cpu
          mem += task.ram
        }
      }
      if (cpu + t.cpu <= node.cpu && mem + t.ram <= node.ram) {
        res :+= n
      }
    }
    res
  }

}
