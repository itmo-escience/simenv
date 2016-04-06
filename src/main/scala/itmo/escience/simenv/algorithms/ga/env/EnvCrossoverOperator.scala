package itmo.escience.simenv.algorithms.ga.env

import java.util
import java.util.{Collections, Random}

import org.uncommons.watchmaker.framework.operators.AbstractCrossover

/**
  * Created by mikhail on 27.01.2016.
  */
class EnvCrossoverOperator (crossoverProb: Double, crossoverPoints: Int = 1)
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
    val p1Size = p1.getNumberOfVariables
    val p2Size = p2.getNumberOfVariables
    val minSize = math.min(p1Size, p2Size)
    val maxSize = math.max(p1Size, p2Size)

    var point = 0
    if (maxSize > 0) {
      point = rnd.nextInt(maxSize)
    }

    val res = new util.ArrayList[EnvConfSolution](2)
    res.add(new EnvConfSolution(p1.genSeq.take(point) ++ p2.genSeq.drop(point), p1.fixedSize))
    res.add(new EnvConfSolution(p1.genSeq.take(point) ++ p2.genSeq.drop(point), p2.fixedSize))
    res
  }

}
