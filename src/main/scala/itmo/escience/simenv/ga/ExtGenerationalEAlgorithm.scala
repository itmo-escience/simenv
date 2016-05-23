package itmo.escience.simenv.ga

import java.util
import java.util.Random

import org.uncommons.watchmaker.framework._

/**
  * Created by mikhail on 02.02.2016.
  */
class ExtGenerationalEAlgorithm(factory: ScheduleCandidateFactory,
                                                      pipeline: EvolutionaryOperator[SSSolution],
                                                      fitnessEvaluator: FitnessEvaluator[SSSolution],
                                                      selector: SelectionStrategy[Object],
                                                      rng: Random, popSize: Int) extends GenerationalEvolutionEngine[SSSolution](factory,
                                                                           pipeline,
                                                                           fitnessEvaluator,
                                                                           selector,
                                                                           rng) {
  override def nextEvolutionStep(evaluatedPopulation: util.List[EvaluatedCandidate[SSSolution]], eliteCount: Int, rng: Random): util.List[EvaluatedCandidate[SSSolution]] = {
    val population :util.ArrayList[SSSolution] = new util.ArrayList[SSSolution](evaluatedPopulation.size())
    val elite: util.ArrayList[SSSolution] = new util.ArrayList[SSSolution](eliteCount)
    val iterator: util.Iterator[EvaluatedCandidate[SSSolution]] = evaluatedPopulation.iterator()

    val nextEvaluatedPopulation: util.List[EvaluatedCandidate[SSSolution]] = new  util.ArrayList[EvaluatedCandidate[SSSolution]]()

    while(elite.size() < eliteCount) {
      elite.add(iterator.next.getCandidate.copy)
    }
    while (iterator.hasNext) {
      nextEvaluatedPopulation.add(iterator.next)
    }

    population.addAll(selector.select(nextEvaluatedPopulation, this.fitnessEvaluator.isNatural, popSize - eliteCount, rng))
    val population1: util.List[SSSolution] = this.pipeline.apply(population, rng)
    population1.addAll(elite)
    evaluatePopulation(population1)
  }
}
