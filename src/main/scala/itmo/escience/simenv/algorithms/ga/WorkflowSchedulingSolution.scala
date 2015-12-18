package itmo.escience.simenv.algorithms.ga

import java.util

import org.uma.jmetal.solution.Solution
import scala.collection.JavaConversions._

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

  def this(that:WorkflowSchedulingSolution) = this(that._genes.toList)

  //TODO: do we play for min or max?
  private var _objective:Double = 0.0

  private val _attributes = new util.HashMap[scala.Any, scala.Any]()

  private val _genes = new util.ArrayList(mappedTasks)

  override def getNumberOfObjectives: Int = 1

  override def setObjective(i: Int, v: Double): Unit = {
    if (i != 0){
      throw new IllegalArgumentException(s"invalid number of objective ${i}. Only 0 is allowed")
    }
    _objective = v

  }

  override def getObjective(i: Int): Double = _objective

  override def getAttribute(o: Object): Object = null//_attributes.get(o)

  override def setAttribute(o: scala.Any, o1: scala.Any): Unit = {
    _attributes.put(o, o1)
  }

  override def setVariableValue(i: Int, t: MappedTask): Unit = {
    _genes.set(i, t)
  }

  override def getVariableValue(i: Int): MappedTask = {
    _genes.get(i)
  }

  override def getVariableValueString(i: Int): String = {
    throw new NotImplementedError()
  }

  override def copy(): WorkflowSchedulingSolution = {
    new WorkflowSchedulingSolution(this)
  }

  override def getNumberOfVariables: Int = _genes.size()

  def tasksSeq() = _genes.toList

}
