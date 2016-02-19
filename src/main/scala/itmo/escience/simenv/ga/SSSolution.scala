package itmo.escience.simenv.ga

import java.util

import scala.collection.JavaConversions._

/**
 * individual for genetic algorithm
 */
class SSSolution(mappedTasks: java.util.HashMap[String, String]) {

  def this(that:SSSolution) = this(that._genes)

  private var _genes: java.util.HashMap[String,String] = mappedTasks

  def setGenes(sol: java.util.HashMap[String, String]) = {
    _genes = sol
  }

  def copy: SSSolution = {
    new SSSolution(this)
  }

  def getNumberOfVariables: Int = _genes.size()

  def genes: java.util.HashMap[String, String] = _genes

  def getVal(k: String) = {
    _genes.get(k)
  }

  def put(k: String, v: String) = {
    _genes.put(k, v)
  }
}
