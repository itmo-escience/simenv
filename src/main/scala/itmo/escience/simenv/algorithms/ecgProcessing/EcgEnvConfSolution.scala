package itmo.escience.simenv.algorithms.ecgProcessing

import java.util

import itmo.escience.simenv.algorithms.ga.EvSolution
import itmo.escience.simenv.algorithms.ga.env.EnvConfSolution
import itmo.escience.simenv.environment.entities.NodeId

import scala.collection.JavaConversions._

/**
 * individual for genetic algorithm
 */
class EcgEnvConfSolution(mappedVms: List[EcgMappedEnv]) extends EvSolution {

  def this(that:EcgEnvConfSolution) = this(that._genes.toList)

  private val _genes = new util.ArrayList(mappedVms)

  def setVariableValue(i: Int, t: EcgMappedEnv): Unit = {
    _genes.set(i, t)
  }

  def getVmElement(vmId: NodeId): EcgMappedEnv = {
    _genes.filter(x => x.vmId == vmId).head
  }

  def addValue(vmId: NodeId, c: Int): Unit = {
    val item = getVmElement(vmId)
    _genes.add(new EcgMappedEnv(vmId=vmId, cores=item.cores + c))
    _genes.remove(item)
  }

//  def deleteValue(i: Int): MappedEnv = {
//    _genes.remove(i)
//  }

  def addDeleteItems(delete: List[EcgMappedEnv], add: List[EcgMappedEnv]) = {
    for (i <- delete) {
      _genes.remove(i)
    }
    for (i <- add) {
      _genes.add(i)
    }
  }

  def getVariableValue(i: Int): EcgMappedEnv = {
    _genes.get(i)
  }

  def getVariableValueString(i: Int): String = {
    throw new NotImplementedError()
  }

  override def copy: EcgEnvConfSolution = {
    val res = new EcgEnvConfSolution(this)
    res.fitness = this.fitness
    res
  }

  def getNumberOfVariables: Int = _genes.size()

  def genSeq: List[EcgMappedEnv] = _genes.toList

  def size = _genes.size

  override var fitness: Double = 66613666
}
