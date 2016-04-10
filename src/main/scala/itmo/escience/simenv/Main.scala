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

//    val wf_list = List[String]("Montage_25", "CyberShake_30", "Inspiral_30", "Sipht_30",
//      "Epigenomics_24", "Montage_50", "CyberShake_50", "Inspiral_50", "Sipht_60")
    val wf_list = List[String]("Montage_25", "Montage_50")
    val rels = List[Double](0.75, 0.85, 0.95)
    val exps = 20
    for (i <- 0 until exps) {
      for (wf <- wf_list) {
        for (rel <- rels) {
          println(s"$wf   $rel   $i")
          val exp = new UrgentCostOptimization(rel, wf)
          exp.run()
        }
      }
    }
  }
}
