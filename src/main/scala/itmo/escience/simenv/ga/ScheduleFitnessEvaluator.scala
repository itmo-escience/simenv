package itmo.escience.simenv.ga

import java.util

import org.uncommons.watchmaker.framework.FitnessEvaluator

import itmo.escience.simenv.entities._
import scala.collection.JavaConversions._

/**
  * Created by mikhail on 22.01.2016.
  */
class ScheduleFitnessEvaluator(env: CarrierNodeEnvironment[CpuRamNode], tasks: util.HashMap[String, DaxTask]) extends FitnessEvaluator[SSSolution] {
  override def isNatural: Boolean = true

  override def getFitness(sol: SSSolution, list: util.List[_ <: SSSolution]): Double = {
    val perfMap: util.HashMap[String, Double] = new util.HashMap[String, Double]()
    val iteratMap: util.HashMap[String, Int] = new util.HashMap[String, Int]()
    val transMap: util.HashMap[(String, String), Double] = new util.HashMap[(String, String), Double]()

    // Evaluate max performance of tasks
    for (t <- sol.genes.keySet()) {
      val item = sol.getVal(t)
      val task = tasks.get(t)
      if (task.children.nonEmpty && task.parents.nonEmpty) {
        val node = env.nodeById(item._1).asInstanceOf[CpuRamNode]
        val cpuCoef = node.cpu * item._2 / task.cpu
        val ramCoef = node.ram * item._2 / task.ram
        val minCoef = math.min(cpuCoef, ramCoef)
        val taskPerf = task.maxData * minCoef
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
          val dataFromParents = task.parents.map(x => x -> iteratMap.get(x) * tasks.get(x).outputData)
          for (d <- dataFromParents) {
            transMap.put((d._1, t), d._2)
          }
          iteratMap.put(t, dataFromParents.map(x => (x._2 / task.inputData.get(x._1)).toInt).min)
        }
        recIteratCounter(task.children)
      }
    }

    recIteratCounter(headTasks.keySet.toList)

    // Result is a sum of all end outputData.
    endTasks.map(x => iteratMap.get(x._1) * x._2.outputData).sum
  }

  def getFitness(t: SSSolution): Double = {
    getFitness(t, null)
  }
}