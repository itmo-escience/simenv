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
    var counter = 0.0
    for (t <- sol.genes.keySet()) {
      counter += sol.getVal(t)._2
    }
    counter
  }

  def getFitness(t: SSSolution): Double = {
    getFitness(t, null)
  }
}