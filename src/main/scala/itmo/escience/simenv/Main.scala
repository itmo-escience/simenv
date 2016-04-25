package itmo.escience.simenv

import java.io.PrintWriter

import itmo.escience.simenv.experiments._
import itmo.escience.simenv.utilities.Units._
import itmo.escience.simenv.utilities.Utilities.generateId

/**
 * Created by Mishanya on 12.10.2015.
 */

/** This is the enter point into the simulator.
  */
object Main {
  def main(args: Array[String]) {

    exp1()
    println("Finished")
  }






  def exp1() = {
//    val wfName = "Montage_25"
//    val wfNames = List("Montage_25", "Montage_50", "CyberShake_30", "CyberShake_50", "Inspiral_30", "Inspiral_50")
    val wfNames = List("Montage_50")
    //    val downTimes = List(0, 10, 25, 50)
    val downTimes = List(10)
    val wfPath = ".\\resources\\wf-examples\\"
    val expPath = ".\\temp\\exps\\"
    //  val basepath = ".\\resources\\"
    //  val wf_name = "crawlerWf"
//    val envArray = List(List(10.0, 15.0, 25.0, 30.0), List(10.0, 15.0, 25.0, 30.0), List(10.0, 15.0, 25.0, 30.0))
//    val envArray = List(List(10.0, 15.0, 25.0, 30.0), List(10.0, 15.0, 25.0, 30.0), List(10.0, 15.0, 25.0, 30.0), List(10.0, 15.0, 25.0, 30.0))
    val envArray = List(List(10.0, 15.0, 25.0, 30.0))

    val globNet = 10 Mbit_Sec
    val locNet = 100 Mbit_Sec
    val reliability = 0.95

    for (downTime <- downTimes) {
      for (i <- 0 until 10) {
        println(s"iterations: $i" )
        for (wf <- wfNames) {
          println("--------")
          println("WF: " + wf)
//          try {
//            println("CGA exp:")
//            val cgaRes = new CGADynamExp(wfPath + wf, envArray, globNet, locNet, reliability, 0, downTime, 0).run()
//            val cgaFile: PrintWriter = new PrintWriter(expPath + "CGA_" + wf + "_" + generateId() + ".txt", "UTF-8")
//            cgaFile.write((cgaRes + ""))
//            cgaFile.close()
//          } catch {
//            case _: Throwable => println("fail")
//          }
          try {
            println("HEFT exp:")
            val heftRes = new HEFTDynamExp(wfPath + wf, envArray, globNet, locNet, reliability, 0, downTime, 0).run()
            val heftFile: PrintWriter = new PrintWriter(expPath + "HEFT_" + wf + "_" + generateId() + ".txt", "UTF-8")
            heftFile.write((heftRes + "\n"))
            heftFile.close()
          } catch {
            case _: Throwable => println("fail")
          }
          try {
            println("GA exp:")
            val gaRes = new GADynamExp(wfPath + wf, envArray, globNet, locNet, reliability, 0, downTime, 0).run()
            val gaFile: PrintWriter = new PrintWriter(expPath + "GA_" + wf + "_" + generateId() + ".txt", "UTF-8")
            gaFile.write((gaRes + "\n"))
            gaFile.close()
          } catch {
            case _: Throwable => println("fail")
          }
//          try {
//            println("HEFT exp:")
//            val heftRes = new HEFTDynamExp(wfPath + wf, envArray, globNet, locNet, reliability, 0, downTime, 0).run()
//            val heftFile: PrintWriter = new PrintWriter(expPath + "HEFT_" + wf + "_" + generateId() + ".txt", "UTF-8")
//            heftFile.write((heftRes + "\n"))
//            heftFile.close()
//          } catch {
//            case _: Throwable => println("fail")
//          }

//
//
//
        }

//
//
//

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
