package itmo.escience.simenv.utilities

import itmo.escience.simenv.environment.entities.{CapacityBasedNode, Node, DaxTask}
import org.apache.commons.math3.special.Erf

/**
  * Created by mikhail on 04.04.2016.
  */
object MathFunctions {
  def getZPercents(daxTask: DaxTask, time: Double, node: Node): Double = {

    val execTime = daxTask.execTime
    val orig = time * node.asInstanceOf[CapacityBasedNode].capacity / 20
    orig / 2 / execTime
//    val sigm = execTime * 0.25
//    val sigm2s = math.sqrt(2*sigm*sigm)
//    val x = (time - execTime) / sigm2s
//    val z = (time - execTime) / sigm

    //    val score = Erf.erf(z) + 0.5
//    val score = 1/2 * (1 + Erf.erf(x))
//    score
  }

  def getZVal(daxTask: DaxTask, perc: Double): Double = {
//    val exTime = daxTask.execTime
    perc * 2 * daxTask.execTime
  }
}
