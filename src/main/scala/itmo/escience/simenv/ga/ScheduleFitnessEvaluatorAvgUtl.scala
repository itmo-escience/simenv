package itmo.escience.simenv.ga

import java.util

import itmo.escience.simenv.entities.{Network, _}
import org.uncommons.watchmaker.framework.FitnessEvaluator

import scala.collection.JavaConversions._

/**
  * Created by mikhail on 22.01.2016.
  */
class ScheduleFitnessEvaluatorAvgUtl(env: CarrierNodeEnvironment[CpuRamNode], tasks: util.HashMap[String, DaxTask]) extends ScheduleFitnessEvaluator(env, tasks) {
  override def isNatural: Boolean = false

  override def getFitness(sol: SSSolution, list: util.List[_ <: SSSolution]): Double = {

    if (sol.genes.size() != tasks.size()) {
      println("Pizdec")
    }

    val perfMap: util.HashMap[String, Double] = new util.HashMap[String, Double]()
    for (g <- sol.genes) {
      val task = g._1
      val node = g._2
      if (!perfMap.containsKey(node)) {
        perfMap.put(node, 0.0)
      }
      perfMap.put(node, perfMap.get(node) + tasks.get(task).cpu)
    }

    var spaceList = List[Double]()
    var overheads = 0.0
    for (p <- perfMap) {
      if (p._2 > env.nodeById(p._1).cpu) {
        overheads += p._2 - env.nodeById(p._1).cpu
      }
      spaceList :+= math.abs(p._2 - env.nodeById(p._1).cpu) / (env.nodeById(p._1).cpu / 100)
    }
    val mean = spaceList.sum / spaceList.size
    val std = spaceList.map(x => math.sqrt((x-mean)*(x-mean))).sum /spaceList.size
    std + overheads

  }
}