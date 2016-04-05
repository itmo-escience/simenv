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

  def maxNodeIdx: Int = {
    var max = 0
    for (item <-_genes) {
      if (item.nodeIdx > max) {
        max = item.nodeIdx
      }
    }
    max
  }

  def addAfter(elem: MappedTask) = {
    val taskId = elem.taskId
    val res = elem.nodeIdx
    var counter = 0
    val idx = _genes.indexWhere(x => x.taskId == taskId)
    _genes.insert(idx, elem)
  }

  def removeGene(idx: Int) = {
    _genes.remove(idx)
  }

  def insertGene(idx: Int, elem: MappedTask) = {
    _genes.insert(idx, elem)
  }

  override var fitness: Double = _
}
