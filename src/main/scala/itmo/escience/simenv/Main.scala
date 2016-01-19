package itmo.escience.simenv

import itmo.escience.simenv.experiments.{GADynamicScheduling, GAStaticScheduling}

/**
 * Created by Mishanya on 12.10.2015.
 */

/** This is the enter point into the simulator.
  */
object Main {
  def main(args: Array[String]) {

    val exp = new GADynamicScheduling()

    exp.init()
    exp.run()
  //TODO:
    // 1. read parameters or config
    // 2. create instance or use object of appropriate Experiment
    // 3. Run experiment
    // 4. Exit
  }
}
