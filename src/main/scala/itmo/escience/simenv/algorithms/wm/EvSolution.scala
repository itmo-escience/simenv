package itmo.escience.simenv.algorithms.wm

/**
  * Created by mikhail on 25.01.2016.
  */
abstract class EvSolution {

  def copy(): EvSolution

  def genSeq()

  var fitness: Double

}
