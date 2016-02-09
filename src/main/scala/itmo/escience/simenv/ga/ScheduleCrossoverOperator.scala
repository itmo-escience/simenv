package itmo.escience.simenv.ga

import java.util
import java.util.{Collections, Random}

import itmo.escience.simenv.entities._
import org.uncommons.watchmaker.framework.operators.AbstractCrossover
import scala.collection.JavaConversions._

/**
  * Created by mikhail on 22.01.2016.
  */
class ScheduleCrossoverOperator(env: CarrierNodeEnvironment[CpuRamNode], tasks: util.HashMap[String, DaxTask], crossProb: Double, crossoverPoints: Int = 1)
  extends AbstractCrossover[SSSolution](crossoverPoints){

  override def apply(parents: util.List[SSSolution], random: Random): util.List[SSSolution] = {
    val selectionClone: util.ArrayList[SSSolution] = new util.ArrayList[SSSolution](parents)
    Collections.shuffle(selectionClone, random)
    val result: util.ArrayList[SSSolution] = new util.ArrayList[SSSolution](parents.size)
    val iterator: util.Iterator[SSSolution] = selectionClone.iterator()
    while(iterator.hasNext) {
      val parent1: SSSolution = iterator.next().copy
      if (iterator.hasNext) {
        val parent2: SSSolution = iterator.next().copy
        if (random.nextDouble() < crossProb) {
          result.addAll(mate(parent1, parent2, crossoverPoints, random))
        }
      }
    }
    result.addAll(parents)
    result
  }

  override def mate(p1: SSSolution, p2: SSSolution, points: Int, rnd: Random): util.List[SSSolution] = {

    var ch1m: util.HashMap[String, (String, Double)] = new util.HashMap[String, (String, Double)]()
    var ch2m: util.HashMap[String, (String, Double)] = new util.HashMap[String, (String, Double)]()

    for (k <- tasks.keySet()) {
      if (rnd.nextBoolean()) {
        ch1m.put(k, p1.getVal(k))
        ch2m.put(k, p2.getVal(k))
      } else {
        ch1m.put(k, p2.getVal(k))
        ch2m.put(k, p1.getVal(k))
      }
    }

    ch1m = StormSchedulingProblem.repairMap(ch1m)
    ch2m = StormSchedulingProblem.repairMap(ch2m)

    val res = new util.ArrayList[SSSolution](2)
    res.add(new SSSolution(ch1m))
    res.add(new SSSolution(ch2m))

    res
  }

}
