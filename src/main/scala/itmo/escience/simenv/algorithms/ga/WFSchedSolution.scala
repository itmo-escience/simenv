package itmo.escience.simenv.algorithms.ga

import java.util

import scala.collection.JavaConversions._

/**
 * individual for genetic algorithm
 */
class WFSchedSolution(mappedTasks: List[MappedTask]) extends EvSolution[MappedTask] {

  def this(that:WFSchedSolution) = this(that._genes.toList)

  private var _genes = new util.ArrayList(mappedTasks)

  def setGenes(sol: WFSchedSolution) = {
    _genes = new util.ArrayList[MappedTask](sol.genSeq)
    fitness = sol.fitness
  }

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
    val res = new WFSchedSolution(this)
    res.fitness = fitness
    res.evaluated = evaluated
    res
  }

  def getNumberOfVariables: Int = _genes.size()

  def genSeq: List[MappedTask] = _genes.toList

  override var fitness: Double = 999999999

  var evaluated: Boolean = false
}
