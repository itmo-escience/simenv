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
//    val res = orig / 2 / execTime
//    if (res > 1.0) {
//      println("Task reliability < 1(((")
//    }
//    res
    val sigm = execTime * 0.25
//    val sigm2s = math.sqrt(2*sigm*sigm)
//    val x = (time - execTime) / sigm2s
    val z = (orig - execTime) / sigm
    val res = 0.5 + integral(laplasFunc, 0, z)
    //    val score = Erf.erf(z) + 0.5
//    val score = 1/2 * (1 + Erf.erf(x))
//    score
    math.floor(res * 100) / 100
  }

  def getZVal(daxTask: DaxTask, perc: Double): Double = {
//    val exTime = daxTask.execTime
    val z = invIntegral(laplasFunc, 0.0, perc)
    val m = daxTask.execTime
    val sigm = m * 0.25
    val res = z * sigm + m
    math.floor(res * 100) / 100
  }

  def laplasFunc(x: Double): Double = 1 / math.sqrt(2 * Math.PI) * Math.exp(-x * x / 2)

  def integral(func: (Double => Double), from: Double, to: Double): Double = {

    val dx = 0.01
    var result = 0.0
    val r = from - to
    var a = from
    var b = a + dx
    var c = a + (b - a) / 2
    while (b < to) {
      val f = func(c) * dx
      result += f
      a += dx
      b += dx
      c = a + (b - a) / 2
    }

    result
  }

  def invIntegral(func: (Double => Double), from: Double, perc: Double): Double = {
    val dx = 0.01
    var result = 0.0
    var interv = 0.0
    var a = from
    var b = a + dx
    var c = a + (b - a) / 2
    while (result < perc - 0.5) {
      val f = func(c) * dx
      result += f
      interv += dx
      a += dx
      b += dx
      c = a + (b - a) / 2
    }

    interv
  }

}
