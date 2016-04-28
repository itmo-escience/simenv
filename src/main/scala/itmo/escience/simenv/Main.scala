package itmo.escience.simenv

import java.io.PrintWriter

import itmo.escience.simenv.experiments._
import itmo.escience.simenv.utilities.RandomWFGenerator
import itmo.escience.simenv.utilities.Units._
import itmo.escience.simenv.utilities.Utilities.generateId
import itmo.escience.simenv.utilities.wfGenAlg.WfGeneratorGA

/**
 * Created by Mishanya on 12.10.2015.
 */

/** This is the enter point into the simulator.
  */
object Main {
  def main(args: Array[String]) {

    val alg = new WfGeneratorGA(crossoverProb=0.5, mutationProb=0.2, popSize=10, iterationCount=100)
    alg.run(5, 1, 3000)

    println("Finished")
  }

  def exp4() = {
    val envArray = List(List(20.0),List(20.0),List(20.0),List(20.0))
    val globNet = 10.0
    val locNet = 100.0

    val repeats = 1

    println("--Type1 experiments--")
    var type1Results = List[Double]()
    for (i <- 0 until repeats) {
      val wf_gen = RandomWFGenerator.type3Generate()
      val exp = new WfStructExp(wf_gen, "bdsm", envArray, globNet, locNet).run()
      type1Results :+= exp
    }
    println(s"Type1 best: ${type1Results.max}")
    println(s"Type1 values: $type1Results")
    println()

    println()
  }

}
