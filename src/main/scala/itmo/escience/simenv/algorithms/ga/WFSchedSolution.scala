package itmo.escience.simenv.algorithms.ga

import java.util

import scala.collection.JavaConversions._

/**
 * individual for genetic algorithm
 */
class WFSchedSolution(mappedTasks: List[MappedTask]) extends EvSolution[MappedTask] {

  def this(that:WFSchedSolution) = this(that._genes.toList)

  private val _genes = new util.ArrayList(mappedTasks)

  def setVariableValue(i: Int, t: MappedTask): Unit = {
    _genes.set(i, t)
  }

  def getVariableValue(i: Int): MappedTask = {
    _genes.get(i)
  }

  def getVariableValueString(i: Int): String = {
    throw new NotImplementedError()
  }

  override def copy: WFSchedSolution = {
    new WFSchedSolution(this)
  }

  def getNumberOfVariables: Int = _genes.size()

  def genSeq: List[MappedTask] = _genes.toList

//  def maxNodeIdx: Int = {
//    var max = 0
//    for (item <-_genes) {
//      if (item.nodeId > max) {
//        max = item.nodeId
//      }
//    }
//    max
//  }

  override var fitness: Double = _
}
