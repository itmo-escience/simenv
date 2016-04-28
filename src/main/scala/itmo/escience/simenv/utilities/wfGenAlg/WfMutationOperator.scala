package itmo.escience.simenv.utilities.wfGenAlg

import java.util
import java.util.Random

import org.uncommons.watchmaker.framework.EvolutionaryOperator

/**
  * Created by mikhail on 22.01.2016.
  */
class WfMutationOperator(probability: Double, left: Int, right: Int) extends EvolutionaryOperator[ArrSolution]{

  override def apply(mutants: util.List[ArrSolution], random: Random): util.List[ArrSolution] = {
    val mutatedPopulation: util.ArrayList[ArrSolution] = new util.ArrayList[ArrSolution](mutants.size())
    val it: util.Iterator[ArrSolution] = mutants.iterator()

    while(it.hasNext) {
      val s: ArrSolution = it.next()
      mutatedPopulation.add(mutateSolution(s, random))
    }
    mutatedPopulation
  }

  def mutateSolution(mutant: ArrSolution, rnd: Random): ArrSolution = {
    if (rnd.nextDouble() <= probability) {
      mutant.evaluated = false
      doMutation(mutant, rnd)
    }
    mutant
  }

  private def doMutation(mutant:ArrSolution, rnd: Random) = {

    val size = mutant.size
    val idx = rnd.nextInt(size)
    val newVal = left + rnd.nextInt(right - left)

    mutant.arr(idx) = newVal
  }
}
