package itmo.escience.simenv

import itmo.escience.simenv.experiments.{CGAStaticExp, HEFTDynamExp, GADynamExp, GAStaticExp}
import itmo.escience.simenv.utilities.Units._

/**
 * Created by Mishanya on 12.10.2015.
 */

/** This is the enter point into the simulator.
  */
object Main {
  def main(args: Array[String]) {

    val wfPath = ".\\resources\\wf-examples\\" + "Montage_75"
    //  val basepath = ".\\resources\\"
    //  val wf_name = "crawlerWf"
    val envArray = List(List(10.0, 15.0, 25.0, 30.0), List(10.0, 15.0, 25.0, 30.0), List(10.0, 15.0, 25.0, 30.0), List(10.0, 15.0, 25.0, 30.0))
//    val envArray = List(List(0.0, 0.0, 0.0, 20.0))

    val globNet = 10 Mbit_Sec
    val locNet = 100 Mbit_Sec
    val reliability = 1
//    for (i <- 0 until 1) {
//      println("GA")
//      new GADynamExp(wfPath, envArray, globNet, locNet, reliability).run()
//      println("HEFT")
//      new HEFTDynamExp(wfPath, envArray, globNet, locNet, reliability).run()
//      println("----")
//    }
    for (i <- 0 until 5) {
      new CGAStaticExp(wfPath, envArray, globNet, locNet, reliability).run()

      new GAStaticExp(wfPath, envArray, globNet, locNet, reliability).run()
      println("==============================")
    }

  //TODO:
    // 1. read parameters or config
    // 2. create instance or use object of appropriate Experiment
    // 3. Run experiment
    // 4. Exit
  }
}
