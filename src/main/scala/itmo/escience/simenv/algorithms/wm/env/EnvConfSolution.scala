package itmo.escience.simenv.algorithms.wm.env

import java.util

import itmo.escience.simenv.algorithms.wm.EvSolution
import itmo.escience.simenv.environment.entities.NodeId
import org.uma.jmetal.solution.Solution

import scala.collection.JavaConversions._

/**
 * individual for genetic algorithm
 */
class EnvConfSolution(mappedVms: List[MappedEnv]) extends EvSolution {

  def this(that:EnvConfSolution) = this(that._genes.toList)

  private val _genes = new util.ArrayList(mappedVms)

  def setVariableValue(i: Int, t: MappedEnv): Unit = {
    _genes.set(i, t)
  }

  def getVmElement(vmId: NodeId) = {
    _genes.filter(x => x.vmId == vmId).head
  }

  def addValue(vmId: NodeId, v: Double): Unit = {
    // TODO optimize this and next function
    val item = _genes.filter(x => x.vmId == vmId).head
    _genes.remove(item)
    _genes.add(new MappedEnv(item.vmId, item.cpuTime + v))
  }

  def addDeleteItems(delete: List[MappedEnv], add: List[MappedEnv]) = {
    for (i <- delete) {
      _genes.remove(i)
    }
    for (i <- add) {
      _genes.add(i)
    }
  }

  def getVariableValue(i: Int): MappedEnv = {
    _genes.get(i)
  }

  def getVariableValueString(i: Int): String = {
    throw new NotImplementedError()
  }

  def copy(): EnvConfSolution = {
    new EnvConfSolution(this)
  }

  def getNumberOfVariables: Int = _genes.size()

  def vmsSeq() = _genes.toList

}
