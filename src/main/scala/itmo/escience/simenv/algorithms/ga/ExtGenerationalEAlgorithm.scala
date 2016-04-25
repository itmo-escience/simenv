package itmo.escience.simenv.algorithms.ga

import java.util
import java.util.Random

import itmo.escience.simenv.environment.entities.{Node, Task}
import org.uncommons.watchmaker.framework._
import scala.collection.JavaConversions._

/**
  * Created by mikhail on 02.02.2016.
  */
class ExtGenerationalEAlgorithm[T <: Task, N <: Node](factory: ScheduleCandidateFactory[T, N],
                                pipeline: EvolutionaryOperator[WFSchedSolution],
                                fitnessEvaluator: FitnessEvaluator[WFSchedSolution],
                                selector: SelectionStrategy[Object],
                                rng: Random, popSize: Int) extends GenerationalEvolutionEngine[WFSchedSolution](factory,
                                                                           pipeline,
                                                                           fitnessEvaluator,
                                                                           selector,
                                                                           rng) {
  override def nextEvolutionStep(evaluatedPopulation: util.List[EvaluatedCandidate[WFSchedSolution]], eliteCount: Int, rng: Random): util.List[EvaluatedCandidate[WFSchedSolution]] = {
    val population :util.ArrayList[WFSchedSolution] = new util.ArrayList[WFSchedSolution](evaluatedPopulation.size())
    val elite: util.ArrayList[WFSchedSolution] = new util.ArrayList[WFSchedSolution](eliteCount)
    val iterator: util.Iterator[EvaluatedCandidate[WFSchedSolution]] = evaluatedPopulation.iterator()

    val nextEvaluatedPopulation: util.List[EvaluatedCandidate[WFSchedSolution]] = new  util.ArrayList[EvaluatedCandidate[WFSchedSolution]]()

    while(elite.size() < eliteCount) {
      elite.add(iterator.next.getCandidate)
    }

    while (iterator.hasNext) {
      nextEvaluatedPopulation.add(iterator.next)
    }

    population.addAll(selector.select(nextEvaluatedPopulation, this.fitnessEvaluator.isNatural, popSize - eliteCount, rng))
    val population1: util.List[WFSchedSolution] = this.pipeline.apply(population, rng)
    population1.addAll(elite)
    val evPop = evaluatePopulation(population1)
    for (it <- evPop) {
      it.getCandidate.fitness = it.getFitness
    }
    evPop
  }
}
