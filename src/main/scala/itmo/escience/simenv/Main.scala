package itmo.escience.simenv

import java.io.PrintWriter

import itmo.escience.simenv.experiments._
import itmo.escience.simenv.utilities.Units._

/**
 * Created by Mishanya on 12.10.2015.
 */

/** This is the enter point into the simulator.
  */
object Main {
  def main(args: Array[String]) {
    val wfName = "Inspiral_30"
    val wfPath = ".\\resources\\wf-examples\\" + wfName
    //  val basepath = ".\\resources\\"
    //  val wf_name = "crawlerWf"

    val nodeResizeTime = 1
    val nodeDownTime = 5
    val resDownTime = 10

    val expPath = ".\\temp\\exps\\"
    val cgaFile: PrintWriter = new PrintWriter(expPath + "CGA2_" + wfName + "_down_" + nodeDownTime + ".txt", "UTF-8")
    val gaFile: PrintWriter = new PrintWriter(expPath + "GA_" + wfName + "_down_" + nodeDownTime + ".txt", "UTF-8")
    val heftFile: PrintWriter = new PrintWriter(expPath + "HEFT_" + wfName + "_down_" + nodeDownTime + ".txt", "UTF-8")

    val envArray = List(List(10.0, 10.0, 10.0),List(10.0, 10.0, 10.0),List(10.0, 10.0, 10.0))
//    val envArray = List(List(30.0, 30.0))
//    val envArray = List(List(0.0, 0.0, 0.0, 20.0))

    val globNet = 10 Mbit_Sec
//    val globNet = 1000 Kbit_Sec
//    val locNet = 10000 Kbit_Sec
    val locNet = 100 Mbit_Sec
    val reliability = 0.95

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
    for (i <- 0 until 2) {
      println("CGA exp:")
      val cgaRes = new CGADynamExp(wfPath, envArray, globNet, locNet, reliability, nodeResizeTime, nodeDownTime, resDownTime).run()
//      println("GA exp:")
//      val gaRes = new GADynamExp(wfPath, envArray, globNet, locNet, reliability, nodeResizeTime, nodeDownTime, resDownTime).run()
//      println("HEFT exp:")
//      val heftRes = new HEFTDynamExp(wfPath, envArray, globNet, locNet, reliability, nodeResizeTime, nodeDownTime, resDownTime).run()

//      cgaFile.write(cgaRes + "\n")
//      gaFile.write(gaRes + "\n")
//      heftFile.write(heftRes + "\n")
    }
    cgaFile.close()
    gaFile.close()
    heftFile.close()
  }
}
