package itmo.escience.simenv

import java.io.PrintWriter
import java.util

import itmo.escience.simenv.experiments._
import itmo.escience.simenv.experiments.ecgProcessing.EcgExp
import itmo.escience.simenv.utilities.Units._

/**
 * Created by Mishanya on 12.10.2015.
 */

/** This is the enter point into the simulator.
  */
object Main {
  def main(args: Array[String]) {

    ecgExp()
    println("Finished")
  }

  def ecgExp() = {
    val wfPath = ".\\resources\\ecgWf\\"
    val wfName = "ecg3"
    val envArray = List(List(8, 0, 0), List(8, 0, 0), List(8, 0, 0))

    val nodeFiles = new util.HashMap[String, List[String]]()
//    nodeFiles.put("res_0", List[String]("raw_ecg_1", "raw_ecg_2"))
    nodeFiles.put("res_0", List[String]("raw_ecg_1", "raw_ecg_3"))
    nodeFiles.put("res_1", List[String]("raw_ecg_2"))

    // Real
//    val globNet = 10 Mbit_Sec
//    val locNet = 65 Mbit_Sec

    val globNet = 10 Mbit_Sec
    val locNet = 65 Mbit_Sec

    val ecgExp = new EcgExp(wfPath + wfName, envArray, nodeFiles, globNet, locNet)
    ecgExp.run()

  }




  def exp1() = {
//    val wfName = "Montage_25"
    val wfNames = List("Montage_100")
    //    val downTimes = List(0, 10, 25, 50)
    val downTimes = List(5)
    val wfPath = ".\\resources\\wf-examples\\"
    val expPath = ".\\temp\\exps\\"
    //  val basepath = ".\\resources\\"
    //  val wf_name = "crawlerWf"
    val envArray = List(List(10.0, 10.0, 20.0), List(15.0, 15.0, 15.0, 15.0))

    val globNet = 10 Mbit_Sec
    val locNet = 100 Mbit_Sec
    val reliability = 1.00

    for (downTime <- downTimes) {
      for (wf <- wfNames) {
        println("WF: " + wf)
//        val cgaFile: PrintWriter = new PrintWriter(expPath + "CGA_" + wf + "_down_" + downTime + ".txt", "UTF-8")
//        val gaFile: PrintWriter = new PrintWriter(expPath + "GA_" + wf + "_down_" + downTime + ".txt", "UTF-8")
//        val heftFile: PrintWriter = new PrintWriter(expPath + "HEFT_" + wf + "_down_" + downTime + ".txt", "UTF-8")

        for (i <- 0 until 1) {
          println("--------")
          println("CGA exp:")
          val cgaRes = new CGADynamExp(wfPath + wf, envArray, globNet, locNet, reliability, 0, downTime, 0).run()
          println("GA exp:")
          val gaRes = new GADynamExp(wfPath + wf, envArray, globNet, locNet, reliability, 0, downTime, 0).run()
          println("HEFT exp:")
          val heftRes = new HEFTDynamExp(wfPath + wf, envArray, globNet, locNet, reliability, 0, downTime, 0).run()

//          heftFile.write((heftRes + "\n").replace(".", ","))
//          cgaFile.write((cgaRes + "\n").replace(".", ","))
//          gaFile.write((gaRes + "\n").replace(".", ","))
        }

//        cgaFile.close()
//        gaFile.close()
//        heftFile.close()

      }
    }

  }

}
