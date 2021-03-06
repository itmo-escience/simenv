package itmo.escience.simenv.algorithms.ga

import java.util
import java.util.{Collections, Random}

import org.uncommons.watchmaker.framework.EvaluatedCandidate
import org.uncommons.watchmaker.framework.operators.AbstractCrossover
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection

import scala.collection.JavaConversions._

/**
  * Created by mikhail on 22.01.2016.
  */
class ScheduleCrossoverOperator(crossoverProb: Double, crossoverPoints: Int = 1)
  extends AbstractCrossover[WFSchedSolution](crossoverPoints){

//  override def apply(parents: util.List[WFSchedSolution], random: Random): util.List[WFSchedSolution] = {
//    val selectionClone: util.ArrayList[WFSchedSolution] = new util.ArrayList[WFSchedSolution](parents)
//    Collections.shuffle(selectionClone, random)
//    val result: util.ArrayList[WFSchedSolution] = new util.ArrayList[WFSchedSolution](parents.size)
//    val iterator: util.Iterator[WFSchedSolution] = selectionClone.iterator()
//    while(iterator.hasNext) {
//
//      val parent1: WFSchedSolution = iterator.next().copy
//      if (iterator.hasNext) {
//        val parent2: WFSchedSolution = iterator.next().copy
//        if (random.nextDouble() < crossoverProb) {
//          result.addAll(mate(parent1, parent2, crossoverPoints, random))
//        }
//      }
//    }
//    result.addAll(parents)
//    result
//  }

  override def apply(parents: util.List[WFSchedSolution], random: Random): util.List[WFSchedSolution] = {

    val selector = new RouletteWheelSelection()

    val selectionClone: util.ArrayList[WFSchedSolution] = new util.ArrayList[WFSchedSolution](parents)
    Collections.shuffle(selectionClone, random)
    val result: util.ArrayList[WFSchedSolution] = new util.ArrayList[WFSchedSolution](parents.size)

    for (i <- 0 until (parents.size * (crossoverProb)).toInt) {
      val xparents = selector.select(selectionClone.map(x => new EvaluatedCandidate[WFSchedSolution](x, x.fitness)), false, 2, random)
      //      if (random.nextDouble() < 0.5) {
      result.addAll(mate(xparents.get(0).asInstanceOf[WFSchedSolution].copy, xparents.get(1).asInstanceOf[WFSchedSolution].copy, crossoverPoints, random))
      //      } else {
      //        result.addAll(mate2(parents.get(0), parents.get(1), crossoverPoints, random))
      //      }
    }

    result.addAll(parents)
    result
  }

  override def mate(p1: WFSchedSolution, p2: WFSchedSolution, points: Int, rnd: Random): util.List[WFSchedSolution] = {
    val size = p1.getNumberOfVariables
    val (left, right) = leftAndRight(size, rnd)

    val genesToBeReplaced_1 = (left until right).map(i => (p1.getVariableValue(i).taskId, i)).toMap
    val genesToBeReplaced_2 = (0 until p2.getNumberOfVariables).map(i => (p2.getVariableValue(i).taskId, i)).
      filter({case (taskId, i)=> genesToBeReplaced_1.contains(taskId)}).toMap

    for ((taskId, i) <- genesToBeReplaced_1) {
      val j = genesToBeReplaced_2(taskId)
      val node_1 = p1.getVariableValue(i).nodeId
      val node_2 = p2.getVariableValue(j).nodeId
      p1.setVariableValue(i, MappedTask(taskId, node_2))
      p2.setVariableValue(j, MappedTask(taskId, node_1))
    }

    val res = new util.ArrayList[WFSchedSolution](2)
    res.add(p1)
    res.add(p2)

    res
  }

  private def leftAndRight(size: Int, rnd: Random) = {
    val a = rnd.nextInt(size)
    val b = rnd.nextInt(size)
    val left = Math.min(a, b)
    val right = Math.max(a,b)
    (left, right)
  }


}
