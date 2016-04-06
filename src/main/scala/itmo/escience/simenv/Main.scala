package itmo.escience.simenv

import itmo.escience.simenv.experiments.{UrgentCostOptimization, CGACloudCostOptimization}
import itmo.escience.simenv.utilities.MathFunctions
import org.apache.commons.math3.special.Erf

/**
 * Created by Mishanya on 12.10.2015.
 */

/** This is the enter point into the simulator.
  */
object Main {
  def main(args: Array[String]) {

    val exp = new UrgentCostOptimization()
    exp.run()

  }
}
