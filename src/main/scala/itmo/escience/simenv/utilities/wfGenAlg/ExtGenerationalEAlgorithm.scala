package itmo.escience.simenv.utilities.wfGenAlg

import java.util
import java.util.Random

import itmo.escience.simenv.environment.entities.{Node, Task}
import org.uncommons.watchmaker.framework._

import scala.collection.JavaConversions._

/**
  * Created by mikhail on 02.02.2016.
  */
class ExtGenerationalEAlgorithm(factory: WfCandidateFactory,
                                                      pipeline: EvolutionaryOperator[ArrSolution],
                                                      fitnessEvaluator: FitnessEvaluator[ArrSolution],
                                                      selector: SelectionStrategy[Object],
                                                      rng: Random, popSize: Int) extends GenerationalEvolutionEngine[ArrSolution](factory,
                                                                           pipeline,
                                                                           fitnessEvaluator,
                                                                           selector,
                                                                           rng) {
  override def nextEvolutionStep(evaluatedPopulation: util.List[EvaluatedCandidate[ArrSolution]], eliteCount: Int, rng: Random): util.List[EvaluatedCandidate[ArrSolution]] = {
    val population :util.ArrayList[ArrSolution] = new util.ArrayList[ArrSolution](evaluatedPopulation.size())
    val elite: util.ArrayList[ArrSolution] = new util.ArrayList[ArrSolution](eliteCount)
    val iterator: util.Iterator[EvaluatedCandidate[ArrSolution]] = evaluatedPopulation.iterator()

    val nextEvaluatedPopulation: util.List[EvaluatedCandidate[ArrSolution]] = new  util.ArrayList[EvaluatedCandidate[ArrSolution]]()

    while(elite.size() < eliteCount) {
      elite.add(iterator.next.getCandidate)
    }

    while (iterator.hasNext) {
      nextEvaluatedPopulation.add(iterator.next)
    }

    population.addAll(selector.select(nextEvaluatedPopulation, this.fitnessEvaluator.isNatural, popSize - eliteCount, rng))
    val population1: util.List[ArrSolution] = this.pipeline.apply(population, rng)
    population1.addAll(elite)
    val evPop = evaluate(population1)
    for (it <- evPop) {
      it.getCandidate.fitness = it.getFitness
      it.getCandidate.evaluated = true
    }
    evPop
  }

  def evaluate(pop: util.List[ArrSolution]): util.List[EvaluatedCandidate[ArrSolution]] = {
    val res = new util.ArrayList[EvaluatedCandidate[ArrSolution]]()
    for (p <- pop) {
      res.add(new EvaluatedCandidate[ArrSolution](p, fitnessEvaluator.getFitness(p, null)))
    }
    res
  }
}
