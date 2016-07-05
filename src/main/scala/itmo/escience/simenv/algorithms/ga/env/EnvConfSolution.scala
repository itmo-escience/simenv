package itmo.escience.simenv.algorithms.ga.env

import java.util

import itmo.escience.simenv.algorithms.ga.EvSolution
import itmo.escience.simenv.environment.entities.NodeId

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

  def getVmElement(vmId: NodeId): MappedEnv = {
    _genes.filter(x => x.vmId == vmId).head
  }

  def addValue(vmId: NodeId, c: Double): Unit = {
    val item = getVmElement(vmId)
    _genes.add(new MappedEnv(vmId=vmId, cap=item.cap + c))
    _genes.remove(item)
  }

//  def deleteValue(i: Int): MappedEnv = {
//    _genes.remove(i)
//  }

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

  override def copy: EnvConfSolution = {
    val res = new EnvConfSolution(this)
    res.fitness = this.fitness
    res
  }

  def getNumberOfVariables: Int = _genes.size()

  def genSeq: List[MappedEnv] = _genes.toList

  def size = _genes.size

  override var fitness: Double = 66613666
}
