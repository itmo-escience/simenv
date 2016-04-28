package itmo.escience.simenv.utilities.wfGenAlg

import java.util

import itmo.escience.simenv.algorithms.ga.env.{EnvConfSolution, EnvConfigurationProblem}
import itmo.escience.simenv.experiments.WfStructExp
import itmo.escience.simenv.utilities.RandomWFGenerator

//import itmo.escience.simenv.algorithms.ga.env.EnvConfSolution
import itmo.escience.simenv.environment.entities.{Context, Node, Task}
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.FitnessEvaluator

/**
  * Created by mikhail on 22.01.2016.
  */
class WfFitnessEvaluator() extends FitnessEvaluator[ArrSolution] {
  override def isNatural: Boolean = true

  override def getFitness(t: ArrSolution, list: util.List[_ <: ArrSolution]): Double = {

    if (t.evaluated) {
      t.fitness
    } else {

      val envArray = List(List(20.0), List(20.0), List(20.0), List(20.0))
      val globNet = 10.0
      val locNet = 100.0

      val wf_gen = RandomWFGenerator.type3GAGenerate(t.arr)
      var fit = new FitExp(wf_gen, "bdsm", envArray, globNet, locNet).run()
      if (fit < 0.01) {
        fit = 0.01
      }
      fit
    }
  }
}