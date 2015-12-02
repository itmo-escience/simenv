package itmo.escience.simenv.algorithms.ga

import java.util

import com.sun.javaws.exceptions.InvalidArgumentException
import org.uma.jmetal.solution.Solution

/**
 * individual for genetic algorithm
 */
class WorkflowSchedulingSolution(mappedTasks: List[MappedTask]) extends Solution[MappedTask]{

  /**
   * "If we consider evolutionary algorithms, a solution represents
   * an individual, the variables constitute the chromosome, and
   * each variable is a gene"
   *          - Redesigning the jMetal Multi-Objective Optimization Framework (p. 2)
   *          http://riuma.uma.es/xmlui/bitstream/handle/10630/10136/gecco2015.pdf?sequence=1
   */

  def this(that:WorkflowSchedulingSolution) = this(that._genes)

  //TODO: do we play for min or max?
  private var _objective:Double = 0.0

  private val _attributes = new util.HashMap[scala.Any, scala.Any]()

  private var _genes = mappedTasks

  override def getNumberOfObjectives: Int = 1

  override def setObjective(i: Int, v: Double): Unit = {
    if (i != 0){
      throw new InvalidArgumentException(Array(s"invalid number of objective ${i}. Only 0 is allowed"))
    }
    _objective = v
  }

  override def getObjective(i: Int): Double = _objective

  override def getAttribute(o: Object): Object = null//_attributes.get(o)

  override def setAttribute(o: scala.Any, o1: scala.Any): Unit = {
    _attributes.put(o, o1)
  }

  override def setVariableValue(i: Int, t: MappedTask): Unit = {
    throw new NotImplementedError()
  }

  override def getVariableValue(i: Int): MappedTask = {
    throw new NotImplementedError()
  }

  override def getVariableValueString(i: Int): String = {
    throw new NotImplementedError()
  }

  override def copy(): Solution[MappedTask] = {
    new WorkflowSchedulingSolution(this)
  }

  override def getNumberOfVariables: Int = _genes.length

  def tasksSeq() = _genes

}
