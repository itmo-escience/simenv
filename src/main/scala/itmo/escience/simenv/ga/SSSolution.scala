package itmo.escience.simenv.ga

import java.util

import scala.collection.JavaConversions._

/**
 * individual for genetic algorithm
 */
class SSSolution(mappedTasks: java.util.HashMap[String, (String, Double)]) {

  def this(that:SSSolution) = this(that._genes)

  private var _genes: java.util.HashMap[String, (String, Double)] = mappedTasks

  def setGenes(sol: java.util.HashMap[String, (String, Double)]) = {
    _genes = sol
  }

  def copy: SSSolution = {
    new SSSolution(this)
  }

  def getNumberOfVariables: Int = _genes.size()

  def genes: java.util.HashMap[String, (String, Double)] = _genes

  def getVal(k: String) = {
    _genes.get(k)
  }

  def put(k: String, v: (String, Double)) = {
    _genes.put(k, v)
  }
}
