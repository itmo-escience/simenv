package itmo.escience.simenv.algorithms.ga.env

import java.util
import java.util.{Collections, Random}

import itmo.escience.simenv.environment.entities.{CapacityBasedCarrier, Node}
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.operators.AbstractCrossover

/**
  * Created by mikhail on 27.01.2016.
  */
class EnvCrossoverOperator[N <: Node] (env: Environment[N], crossoverProb: Double, crossoverPoints: Int = 1)
  extends AbstractCrossover[EnvConfSolution](crossoverPoints){

  override def apply(parents: util.List[EnvConfSolution], random: Random): util.List[EnvConfSolution] = {
    val selectionClone: util.ArrayList[EnvConfSolution] = new util.ArrayList[EnvConfSolution](parents)
    Collections.shuffle(selectionClone, random)
    val result: util.ArrayList[EnvConfSolution] = new util.ArrayList[EnvConfSolution](parents.size())
    val iterator: util.Iterator[EnvConfSolution] = selectionClone.iterator()

    while(iterator.hasNext) {
      val parent1: EnvConfSolution = iterator.next().copy()
      if(iterator.hasNext) {
        val parent2: EnvConfSolution = iterator.next().copy()
        if (random.nextDouble() < crossoverProb) {
          result.addAll(mate(parent1, parent2, crossoverPoints, random))
        }
      }
    }
    result.addAll(parents)
    result
  }

  override def mate(p1: EnvConfSolution, p2: EnvConfSolution, points: Int, rnd: Random): util.List[EnvConfSolution] = {
    val nodes = env.carriers
    if (nodes.size > 1) {
      val n1 = nodes(rnd.nextInt(nodes.size))
      val n2 = nodes.filter(x => x.id != n1.id)(rnd.nextInt(nodes.size - 1))
      val n1vms = n1.children.map(x => x.id)
      val n2vms = n2.children.map(x => x.id)
      val p1n1 = p1.genSeq.filter(x => n1vms.contains(x.vmId))
      val p1n2 = p1.genSeq.filter(x => n2vms.contains(x.vmId))
      val p2n1 = p2.genSeq.filter(x => n1vms.contains(x.vmId))
      val p2n2 = p2.genSeq.filter(x => n2vms.contains(x.vmId))
      p1.addDeleteItems(p1n1, p2n1)
      p2.addDeleteItems(p2n2, p1n2)
    }

    val ret = new util.ArrayList[EnvConfSolution](2)
    ret.add(p1)
    ret.add(p2)

    ret
  }

}
