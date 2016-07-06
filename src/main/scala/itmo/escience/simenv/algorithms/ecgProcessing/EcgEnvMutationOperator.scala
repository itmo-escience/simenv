package itmo.escience.simenv.algorithms.ecgProcessing

import java.util
import java.util.Random

import itmo.escience.simenv.environment.ecgProcessing.CoreStorageNode
import itmo.escience.simenv.environment.entities.{CapacityBasedCarrier, Node, NodeStatus}
import itmo.escience.simenv.environment.entitiesimpl.CarrierNodeEnvironment
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.EvolutionaryOperator

/**
  * Created by mikhail on 27.01.2016.
  */
class EcgEnvMutationOperator(env: CarrierNodeEnvironment[CoreStorageNode],
                                        probability: Double)
  extends EvolutionaryOperator[EcgEnvConfSolution] {

  override def apply(mutants: util.List[EcgEnvConfSolution], random: Random): util.List[EcgEnvConfSolution] = {
    val mutatedPopulation: util.ArrayList[EcgEnvConfSolution] = new util.ArrayList[EcgEnvConfSolution](mutants.size())
    val it: util.Iterator[EcgEnvConfSolution] = mutants.iterator()

    while(it.hasNext) {
      val s: EcgEnvConfSolution = it.next()
      mutatedPopulation.add(mutateSolution(s, random))
    }
    mutatedPopulation
  }

  def mutateSolution(mutant: EcgEnvConfSolution, rnd: Random): EcgEnvConfSolution = {
    if (rnd.nextDouble() <= probability) {
      doMutation(mutant, rnd)
    }

    mutant
  }

  def doMutation(mutant:EcgEnvConfSolution, rnd: Random) = {

    val nodes = env.carriers.filter(x => x.children.count(y => y.status == NodeStatus.UP) > 1)
    if (nodes.nonEmpty) {
      val node = nodes(rnd.nextInt(nodes.length))
      val availableNodes = node.children.filter(x => x.status == NodeStatus.UP)
      val vmNumber = availableNodes.size
      val nodeChildren = mutant.genSeq.filter(x => availableNodes.map(y => y.id).contains(x.vmId))
      val workChildren = nodeChildren.filter(x => x.cores > 0)
      val vmFrom = workChildren(rnd.nextInt(workChildren.size))
      val restChildren = nodeChildren.filter(x => x.vmId != vmFrom.vmId)
      val vmTo = restChildren(rnd.nextInt(restChildren.size))
      var cpuTrans: Int = 0
      val option = rnd.nextInt(3)
      option match {
        case 0 => cpuTrans = rnd.nextInt(vmFrom.cores) + 1
        case 1 => cpuTrans = vmFrom.cores
        case 2 => cpuTrans = vmFrom.cores / 2
      }
      mutant.addValue(vmFrom.vmId, -cpuTrans)
      mutant.addValue(vmTo.vmId, cpuTrans)
    }
  }

}
