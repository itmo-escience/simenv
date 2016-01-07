package itmo.escience.simenv.algorithms.ga.vmga

import java.util

import itmo.escience.simenv.environment.entities.NodeId
import org.uma.jmetal.solution.Solution

import scala.collection.JavaConversions._

/**
 * individual for genetic algorithm
 */
class EnvConfigurationSolution(mappedVms: List[MappedVm]) extends Solution[MappedVm]{

  def this(that:EnvConfigurationSolution) = this(that._genes.toList)

  //TODO: do we play for min or max?
  private var _objective:Double = 0.0

  private val _attributes = new util.HashMap[scala.Any, scala.Any]()

  private val _genes = new util.ArrayList(mappedVms)

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

  override def setVariableValue(i: Int, t: MappedVm): Unit = {
    _genes.set(i, t)
  }


  def addCoresValue(vmId: NodeId, v: Int): Unit = {
    // TODO optimize this and next function
    val item = _genes.filter(x => x.vmId == vmId).head
    _genes.remove(item)
    _genes.add(new MappedVm(item.vmId, (item.config._1 + v, item.config._2)))
  }

  def addRamValue(vmId: NodeId, v: Int): Unit = {
    val item = _genes.filter(x => x.vmId == vmId).head
    _genes.remove(item)
    _genes.add(new MappedVm(item.vmId, (item.config._1, item.config._2 + v)))
  }

  def addDeleteItems(delete: List[MappedVm], add: List[MappedVm]) = {
    for (i <- delete) {
      _genes.remove(i)
    }
    for (i <- add) {
      _genes.add(i)
    }
  }

  override def getVariableValue(i: Int): MappedVm = {
    _genes.get(i)
  }

  override def getVariableValueString(i: Int): String = {
    throw new NotImplementedError()
  }

  override def copy(): EnvConfigurationSolution = {
    new EnvConfigurationSolution(this)
  }

  override def getNumberOfVariables: Int = _genes.size()

  def vmsSeq() = _genes.toList

}
