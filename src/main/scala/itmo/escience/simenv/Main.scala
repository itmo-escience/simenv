package itmo.escience.simenv

import itmo.escience.simenv.experiments._
import itmo.escience.simenv.utilities.Units._

/**
 * Created by Mishanya on 12.10.2015.
 */

/** This is the enter point into the simulator.
  */
object Main {
  def main(args: Array[String]) {

    val wfPath = ".\\resources\\wf-examples\\" + "Montage_25"
    //  val basepath = ".\\resources\\"
    //  val wf_name = "crawlerWf"
    val envArray = List(List(10.0, 15.0, 25.0),List(10.0, 15.0, 25.0), List(10.0, 15.0, 25.0))
//    val envArray = List(List(30.0, 30.0))
//    val envArray = List(List(0.0, 0.0, 0.0, 20.0))

    val globNet = 10 Mbit_Sec
//    val globNet = 1000 Kbit_Sec
//    val locNet = 10000 Kbit_Sec
    val locNet = 1000 Mbit_Sec
    val reliability = 0.95

    val nodeResizeTime = 1
    val nodeDownTime = 5
    val resDownTime = 10
//    for (i <- 0 until 1) {
//      println("GA")
//      new GADynamExp(wfPath, envArray, globNet, locNet, reliability).run()
//      println("HEFT")
//      new HEFTDynamExp(wfPath, envArray, globNet, locNet, reliability).run()
//      println("----")
//    }
//    for (i <- 0 until 1) {
//      new CGAStaticExp(wfPath, envArray, globNet, locNet, reliability).run()
//
//      new GAStaticExp(wfPath, envArray, globNet, locNet, reliability).run()
//      println("==============================")
//    }
    for (i <- 0 until 1) {
      println("CGA exp:")
      new CGADynamExp(wfPath, envArray, globNet, locNet, reliability, nodeResizeTime, nodeDownTime, resDownTime).run()
      println("GA exp:")
      new GADynamExp(wfPath, envArray, globNet, locNet, reliability, nodeResizeTime, nodeDownTime, resDownTime).run()
      println("HEFT exp:")
      new HEFTDynamExp(wfPath, envArray, globNet, locNet, reliability, nodeResizeTime, nodeDownTime, resDownTime).run()
    }
  }
}
