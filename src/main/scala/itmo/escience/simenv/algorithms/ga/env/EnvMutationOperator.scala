package itmo.escience.simenv.algorithms.ga.env

import java.util
import java.util.Random

import itmo.escience.simenv.environment.entities.{CapacityBasedCarrier, Node}
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.EvolutionaryOperator

/**
  * Created by mikhail on 27.01.2016.
  */
class EnvMutationOperator[N <: Node](env: Environment[N],
                          probability: Double)
  extends EvolutionaryOperator[EnvConfSolution] {

  override def apply(mutants: util.List[EnvConfSolution], random: Random): util.List[EnvConfSolution] = {
    val mutatedPopulation: util.ArrayList[EnvConfSolution] = new util.ArrayList[EnvConfSolution](mutants.size())
    val it: util.Iterator[EnvConfSolution] = mutants.iterator()

    while(it.hasNext) {
      val s: EnvConfSolution = it.next()
      mutatedPopulation.add(mutateSolution(s, random))
    }
    mutatedPopulation
  }

  def mutateSolution(mutant: EnvConfSolution, rnd: Random): EnvConfSolution = {
    if (rnd.nextDouble() <= probability) {
      doMutation(mutant, rnd)
    }

    mutant
  }

  def doMutation(mutant:EnvConfSolution, rnd: Random) = {
//    val option: Int = rnd.nextInt(3)
//    option match {
//      case 0 => changeNode(mutant, rnd)
//      case 1 => addNode(mutant, rnd)
//      case 2 => deleteNode(mutant, rnd)
//    }
//    mutant

    val nodes = env.carriers.filter(x => x.children.size > 1)
    if (nodes.nonEmpty) {
      val node = nodes(rnd.nextInt(nodes.length)).asInstanceOf[CapacityBasedCarrier]
      val vmNumber = node.children.size
      val nodeChildren = mutant.genSeq.filter(x => node.children.map(y => y.id).contains(x.vmId))
      val workChildren = nodeChildren.filter(x => x.cap > 0)
      val vmFrom = workChildren(rnd.nextInt(workChildren.size))
      val restChildren = nodeChildren.filter(x => x.vmId != vmFrom.vmId)
      val vmTo = restChildren(rnd.nextInt(restChildren.size))
      var cpuTrans: Double = 0
      if (rnd.nextBoolean()) {
        cpuTrans = rnd.nextInt(vmFrom.cap.toInt).toDouble
      } else {
        cpuTrans = vmFrom.cap
      }
      mutant.addValue(vmFrom.vmId, -cpuTrans)
      mutant.addValue(vmTo.vmId, cpuTrans)
    }
  }

//  def changeNode(mutant:EnvConfSolution, rnd: Random) = {
//    val idx = rnd.nextInt(mutant.getNumberOfVariables)
//    val newCap = nodesTypes(rnd.nextInt(nodesTypes.size))
//    mutant.setVariableValue(idx, new MappedEnv(newCap))
//  }
//
//  def addNode(mutant:EnvConfSolution, rnd: Random) = {
//    mutant.addValue(nodesTypes(rnd.nextInt(nodesTypes.size)))
//  }
//
//  def deleteNode(mutant:EnvConfSolution, rnd: Random) = {
//    mutant.deleteValue(rnd.nextInt(mutant.getNumberOfVariables))
//  }
}
