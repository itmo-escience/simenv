package itmo.escience.simenv.algorithms.ga

/**
  * Created by mikhail on 25.01.2016.
  */
abstract class EvSolution[T] {

  def copy(): EvSolution[T]

//  def genSeq(): List[T]

  var fitness: Double

}
