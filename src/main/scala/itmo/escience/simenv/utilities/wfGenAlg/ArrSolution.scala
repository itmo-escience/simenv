package itmo.escience.simenv.utilities.wfGenAlg

/**
  * Created by mikhail on 28.04.2016.
  */
class ArrSolution(init: Array[Int]) {
  var arr: Array[Int] = init
  var fitness: Double = 0.01
  var evaluated: Boolean = false

  def size = arr.length

}
