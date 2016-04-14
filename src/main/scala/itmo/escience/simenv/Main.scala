package itmo.escience.simenv

import java.io.PrintWriter

import ifmo.escience.dapris.common.data.MockRepository
import ifmo.escience.dapris.common.entities.{Workload, Environment}
import ifmo.escience.dapris.common.sample.SampleAlgorithm
import itmo.escience.simenv.experiments._
import itmo.escience.simenv.utilities.Units._
import org.apache.commons.math3.special.Erf

/**
 * Created by Mishanya on 12.10.2015.
 */

/** This is the enter point into the simulator.
  */
object Main {
  def main(args: Array[String]) {

//    val qwe =Erf.erf(0.95)
    exp1()

//    val sampleEnv = new Environment()
//    val sampleAlg = new SampleAlgorithm()
//    val sampleRepo = new MockRepository()
//    val sampleWorkload = new Workload(sampleRepo.getAllTasks)
//
//
//    val ip = new IPRunner(sampleEnv, sampleAlg, null)
//    ip.run()
//    println("f")

  }






  def exp1() = {
//    val wfName = "Montage_25"
    val wfNames = List("Montage_25")
    //    val downTimes = List(0, 10, 25, 50)
    val downTimes = List(5)
    val wfPath = ".\\resources\\wf-examples\\"
    val expPath = ".\\temp\\exps\\"
    //  val basepath = ".\\resources\\"
    //  val wf_name = "crawlerWf"
    val envArray = List(List(10.0, 15.0, 25.0, 30.0))

    val globNet = 10 Mbit_Sec
    val locNet = 1000 Mbit_Sec
    val reliability = 1.00

    for (downTime <- downTimes) {
      for (wf <- wfNames) {
        println("WF: " + wf)
//        val cgaFile: PrintWriter = new PrintWriter(expPath + "CGA_" + wf + "_down_" + downTime + ".txt", "UTF-8")
//        val gaFile: PrintWriter = new PrintWriter(expPath + "GA_" + wf + "_down_" + downTime + ".txt", "UTF-8")
//        val heftFile: PrintWriter = new PrintWriter(expPath + "HEFT_" + wf + "_down_" + downTime + ".txt", "UTF-8")

        for (i <- 0 until 1) {
          println("--------")
//          println("CGA exp:")
//          val cgaRes = new CGADynamExp(wfPath + wf, envArray, globNet, locNet, reliability, 0, downTime, 0).run()
          println("GA exp:")
          val gaRes = new GADynamExp(wfPath + wf, envArray, globNet, locNet, reliability, 0, downTime, 0).run()
//          println("HEFT exp:")
//          val heftRes = new HEFTDynamExp(wfPath + wf, envArray, globNet, locNet, reliability, 0, downTime, 0).run()

//          heftFile.write((heftRes + "\n").replace(".", ","))
//          cgaFile.write((cgaRes + "\n").replace(".", ","))
//          gaFile.write((gaRes + "\n").replace(".", ","))
        }

//        cgaFile.close()
//        gaFile.close()
//        heftFile.close()

      }
    }

    def exp2() = {
      val wfName = "Montage_50"
      val downTime = 0
      val wfPath = ".\\resources\\wf-examples\\"
      val expPath = ".\\temp\\exps\\"
      val envArray = List(List(10.0, 10.0, 20.0), List(10.0, 10.0, 20.0))

      val globNet = 10 Mbit_Sec
      val locNet = 1000 Mbit_Sec
      val reliability = 1
      val nodeDownTime = 5
      val resDownTime = 10

      val heftExp = new HEFTDynamExp(wfPath + wfName, envArray, globNet, locNet, reliability, 0, nodeDownTime, resDownTime)
      heftExp.run()

      val gaDynamExp = new GADynamExp(wfPath + wfName, envArray, globNet, locNet, reliability, 0, nodeDownTime, resDownTime)
      gaDynamExp.run()

      val cgaDynamExp = new CGADynamExp(wfPath + wfName, envArray, globNet, locNet, reliability, 0, nodeDownTime, resDownTime)
      cgaDynamExp.run()
    }
  }

}
