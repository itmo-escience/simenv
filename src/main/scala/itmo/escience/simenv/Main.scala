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
    val wfName = "Montage_50"
    val wfNames = List("Montage_25", "Montage_50", "CyberShake_30", "CyberShake_50", "Inspiral_30", "Inspiral_50")
    val downTimes = List(0, 5, 10, 15, 20)
    val wfPath = ".\\resources\\wf-examples\\"
    val expPath = ".\\temp\\exps\\"
    //  val basepath = ".\\resources\\"
    //  val wf_name = "crawlerWf"
    val envArray = List(List(10.0, 10.0, 20.0), List(10.0, 10.0, 20.0))

    val globNet = 10 Mbit_Sec
    val locNet = 1000 Mbit_Sec
    val reliability = 0.95

    for (downTime <- downTimes) {
      for (wf <- wfNames) {

        val cgaFile: PrintWriter = new PrintWriter(expPath + "CGA_" + wfName + "_down_" + downTime + ".txt", "UTF-8")
        val gaFile: PrintWriter = new PrintWriter(expPath + "GA_" + wfName + "_down_" + downTime + ".txt", "UTF-8")
        val heftFile: PrintWriter = new PrintWriter(expPath + "HEFT_" + wfName + "_down_" + downTime + ".txt", "UTF-8")

        for (i <- 0 until 10) {
          println("---------")
          println("CGA exp:")
          val cgaRes = new CGADynamExp(wfPath + wfName, envArray, globNet, locNet, reliability, 0, downTime, 0).run()
          println("GA exp:")
          val gaRes = new GADynamExp(wfPath + wfName, envArray, globNet, locNet, reliability, 0, downTime, 0).run()
          println("HEFT exp:")
          val heftRes = new HEFTDynamExp(wfPath + wfName, envArray, globNet, locNet, reliability, 0, downTime, 0).run()

          cgaFile.write((cgaRes + "\n").replace(".", ","))
          gaFile.write((gaRes + "\n").replace(".", ","))
          heftFile.write((heftRes + "\n").replace(".", ","))
        }

        cgaFile.close()
        gaFile.close()
        heftFile.close()

      }
    }
  }
}
