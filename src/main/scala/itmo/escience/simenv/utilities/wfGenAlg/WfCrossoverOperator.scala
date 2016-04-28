package itmo.escience.simenv.utilities.wfGenAlg

import java.util
import java.util.{Collections, Random}

import org.uncommons.watchmaker.framework.EvaluatedCandidate
import org.uncommons.watchmaker.framework.operators.AbstractCrossover
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection

import scala.collection.JavaConversions._

/**
  * Created by mikhail on 22.01.2016.
  */
class WfCrossoverOperator(crossoverProb: Double, crossoverPoints: Int = 1)
  extends AbstractCrossover[ArrSolution](crossoverPoints){


  override def apply(parents: util.List[ArrSolution], random: Random): util.List[ArrSolution] = {

    val selector = new RouletteWheelSelection()

    val result: util.ArrayList[ArrSolution] = new util.ArrayList[ArrSolution]()

    for (i <- 0 until (parents.size * crossoverProb).toInt) {
      val locParents = selector.select(parents.map(x => new EvaluatedCandidate[ArrSolution](x, x.fitness)), false, 2, random)
      result.addAll(mate(locParents.get(0), locParents.get(1), 1, random))
    }

    result.addAll(parents)
    result
  }

  override def mate(p1: ArrSolution, p2: ArrSolution, points: Int, rnd: Random): util.List[ArrSolution] = {
    val size = p1.size
    val point = rnd.nextInt(size)

    val child1 = new Array[Int](size)
    val child2 = new Array[Int](size)
    for (i <- child1.indices) {
      if (i < point) {
        child1(i) = p1.arr(i)
        child2(i) = p2.arr(i)
      } else {
        child1(i) = p2.arr(i)
        child2(i) = p1.arr(i)
      }
    }

    val res = new util.ArrayList[ArrSolution](2)
    res.add(new ArrSolution(child1))
    res.add(new ArrSolution(child2))
    res
  }

}
