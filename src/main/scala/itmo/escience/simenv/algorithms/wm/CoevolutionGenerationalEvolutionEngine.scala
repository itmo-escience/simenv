package itmo.escience.simenv.algorithms.wm

import java.util
import java.util.{List, Random}

import org.uncommons.watchmaker.framework._
import scala.collection.JavaConversions
import scala.collection.JavaConversions._

/**
  * Created by mikhail on 25.01.2016.
  */
class CoevolutionGenerationalEvolutionEngine[T](schedFactory: CandidateFactory[T], envFactory: CandidateFactory[T],
   schedOperators: EvolutionaryOperator[T], envOperators: EvolutionaryOperator[T],
   fitnessEvaluator: FitnessEvaluator[T],
   selectionStrategy: SelectionStrategy[T], rng: Random) extends GenerationalEvolutionEngine[T](schedFactory,
  schedOperators, fitnessEvaluator, selectionStrategy, rng) {

  var evSchedPop: util.List[EvaluatedCandidate[T]] = null
  var evEnvPop: util.List[EvaluatedCandidate[T]] = null
  var best: (WFSchedSolution, WFSchedSolution) = null

  override def nextEvolutionStep(evaluatedPopulation: util.List[EvaluatedCandidate[T]], eliteCount: Int, rng: Random) = {
    val population: util.List[T]  = new util.ArrayList[T](evaluatedPopulation.size())
    val elite: util.List[T] = new util.ArrayList[T](eliteCount)
    val iterator: util.Iterator[EvaluatedCandidate[T]] = evaluatedPopulation.iterator()

    while(elite.size() < eliteCount) {
      elite.add(iterator.next().getCandidate)
    }

    population.addAll(selectionStrategy.select(evaluatedPopulation, fitnessEvaluator.isNatural, evaluatedPopulation.size() - eliteCount, rng))
    val population1 : util.List[T] = evolutionScheme.apply(population.asInstanceOf[util.List[T]], rng)
    population1.addAll(elite)
    evaluatePopulation(population1)
  }

  override def evaluatePopulation(population: util.List[T]): util.List[EvaluatedCandidate[T]] = super.evaluatePopulation(population)

  override def evolvePopulation(populationSize: Int, eliteCount: Int, seedCandidates: util.Collection[T], conditions: TerminationCondition*): util.List[EvaluatedCandidate[T]] = {
    if (eliteCount >= 0 && eliteCount < populationSize) {
      if(conditions.isEmpty) {
        throw new IllegalArgumentException("At least one TerminationCondition must be specified.");
      } else {
//        this.satisfiedTerminationConditions = null
        var gen: Int = 0
        var startTime: Double = System.currentTimeMillis()

        val schedSeed: util.List[T] = seedCandidates.toList.filter(x => x.isInstanceOf[WFSchedSolution])
        val envSeed: util.List[T] = seedCandidates.toList.filter(x => !x.isInstanceOf[WFSchedSolution])

        var schedPop: util.List[T] = schedFactory.generateInitialPopulation(populationSize, seedCandidates, rng)
        var envPop: util.List[T] = envFactory.generateInitialPopulation(populationSize, envSeed, rng)

        var pairs:
      }
    }


    if(eliteCount >= 0 && eliteCount < populationSize) {
      if(conditions.length == 0) {
        throw new IllegalArgumentException("At least one TerminationCondition must be specified.");
      } else {

        this.satisfiedTerminationConditions = null;
        int currentGenerationIndex = 0;
        long startTime = System.currentTimeMillis();
        List population = this.candidateFactory.generateInitialPopulation(populationSize, seedCandidates, this.rng);
        List evaluatedPopulation = this.evaluatePopulation(population);
        EvolutionUtils.sortEvaluatedPopulation(evaluatedPopulation, this.fitnessEvaluator.isNatural());
        PopulationData data = EvolutionUtils.getPopulationData(evaluatedPopulation, this.fitnessEvaluator.isNatural(), eliteCount, currentGenerationIndex, startTime);
        this.notifyPopulationChange(data);

        List satisfiedConditions;
        for(satisfiedConditions = EvolutionUtils.shouldContinue(data, conditions); satisfiedConditions == null; satisfiedConditions = EvolutionUtils.shouldContinue(data, conditions)) {
          ++currentGenerationIndex;
          evaluatedPopulation = this.nextEvolutionStep(evaluatedPopulation, eliteCount, this.rng);
          EvolutionUtils.sortEvaluatedPopulation(evaluatedPopulation, this.fitnessEvaluator.isNatural());
          data = EvolutionUtils.getPopulationData(evaluatedPopulation, this.fitnessEvaluator.isNatural(), eliteCount, currentGenerationIndex, startTime);
          this.notifyPopulationChange(data);
        }

        this.satisfiedTerminationConditions = satisfiedConditions;
        return evaluatedPopulation;
      }
    } else {
      throw new IllegalArgumentException("Elite count must be non-negative and less than population size.");
    }
  }
}

