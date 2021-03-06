package itmo.escience.simenv.algorithms.ga.env

import java.util
import java.util.Random

import itmo.escience.simenv.environment.entities.{NodeStatus, CapacityBasedCarrier, Node}
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

    val nodes = env.carriers.filter(x => x.children.count(y => y.status == NodeStatus.UP) > 1)
    if (nodes.nonEmpty) {
      val node = nodes(rnd.nextInt(nodes.length)).asInstanceOf[CapacityBasedCarrier]
      val availableNodes = node.children.filter(x => x.status == NodeStatus.UP)
      val vmNumber = availableNodes.size
      val nodeChildren = mutant.genSeq.filter(x => availableNodes.map(y => y.id).contains(x.vmId))
      val workChildren = nodeChildren.filter(x => x.cap > 0)
      val vmFrom = workChildren(rnd.nextInt(workChildren.size))
      val restChildren = nodeChildren.filter(x => x.vmId != vmFrom.vmId)
      val vmTo = restChildren(rnd.nextInt(restChildren.size))
      var cpuTrans: Double = 0
      val option = rnd.nextInt(3)
      option match {
        case 0 => cpuTrans = rnd.nextInt(vmFrom.cap.toInt).toDouble + 1
        case 1 => cpuTrans = vmFrom.cap
        case 2 => cpuTrans = (vmFrom.cap / 2).toInt.toDouble
      }
//      if (rnd.nextBoolean()) {
//        cpuTrans = rnd.nextInt(vmFrom.cap.toInt).toDouble + 1
//      } else {
//        cpuTrans = vmFrom.cap
//      }
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
