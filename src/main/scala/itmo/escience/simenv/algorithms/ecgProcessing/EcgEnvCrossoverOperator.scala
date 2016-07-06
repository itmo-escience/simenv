package itmo.escience.simenv.algorithms.ecgProcessing

import java.util
import java.util.{Collections, Random}

import itmo.escience.simenv.algorithms.ga.env.EnvConfSolution
import itmo.escience.simenv.environment.entities.Node
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.EvaluatedCandidate
import org.uncommons.watchmaker.framework.operators.AbstractCrossover
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection

import scala.collection.JavaConversions._

/**
  * Created by mikhail on 27.01.2016.
  */
class EcgEnvCrossoverOperator[N <: Node](env: Environment[N], crossoverProb: Double, crossoverPoints: Int = 1)
  extends AbstractCrossover[EcgEnvConfSolution](crossoverPoints){

  override def apply(parents: util.List[EcgEnvConfSolution], random: Random): util.List[EcgEnvConfSolution] = {

    val selector = new RouletteWheelSelection()

    val selectionClone: util.ArrayList[EcgEnvConfSolution] = new util.ArrayList[EcgEnvConfSolution](parents)
    Collections.shuffle(selectionClone, random)
    val result: util.ArrayList[EcgEnvConfSolution] = new util.ArrayList[EcgEnvConfSolution](parents.size)

    for (i <- 0 until (parents.size * (crossoverProb)).toInt) {
      val xparents = selector.select(selectionClone.map(x => new EvaluatedCandidate[EcgEnvConfSolution](x, x.fitness)), false, 2, random)
      //      if (random.nextDouble() < 0.5) {
      result.addAll(mate(xparents.get(0).asInstanceOf[EcgEnvConfSolution].copy, xparents.get(1).asInstanceOf[EcgEnvConfSolution].copy, crossoverPoints, random))
      //      } else {
      //        result.addAll(mate2(parents.get(0), parents.get(1), crossoverPoints, random))
      //      }
    }

    result.addAll(parents)
    result
  }

  override def mate(p1: EcgEnvConfSolution, p2: EcgEnvConfSolution, points: Int, rnd: Random): util.List[EcgEnvConfSolution] = {
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

    val ret = new util.ArrayList[EcgEnvConfSolution](2)
    ret.add(p1)
    ret.add(p2)

    ret
  }

}
