package itmo.escience.simenv.algorithms.ultraGA

import java.util

import scala.collection.JavaConversions._

/**
 * individual for genetic algorithm
 */
class MishanyaSolution(mappedTasks: List[MMappedTask]) {

  def this(that:MishanyaSolution) = this(that._genes.toList)

  private var _genes = new util.ArrayList(mappedTasks)

  def setGenes(sol: MishanyaSolution) = {
    _genes = new util.ArrayList[MMappedTask](sol.genSeq)
    fitness = sol.fitness
  }

  def setVariableValue(i: Int, t: MMappedTask): Unit = {
    _genes.set(i, t)
  }

  def getVariableValue(i: Int): MMappedTask = {
    _genes.get(i)
  }

  def getVariableValueString(i: Int): String = {
    throw new NotImplementedError()
  }

  def copy: MishanyaSolution = {
    val newGenes = new util.ArrayList[MMappedTask]()
    for ( gen <- _genes) {
      newGenes.add(new MMappedTask(gen.taskId, gen.nodeId))
    }
    val res = new MishanyaSolution(newGenes.toList)
    res.fitness = fitness
    res.evaluated = evaluated
    res
  }

  def getNumberOfVariables: Int = _genes.size()

  def genSeq: List[MMappedTask] = _genes.toList

  var fitness: Double = 999999999

  var evaluated: Boolean = false
}
