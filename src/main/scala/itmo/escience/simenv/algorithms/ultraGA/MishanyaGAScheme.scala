package itmo.escience.simenv.algorithms.ultraGA

import java.util
import java.util.Random

import itmo.escience.simenv.environment.entities.{DaxTask, CapacityBasedNode, Node, Task}
import org.uncommons.watchmaker.framework._

import scala.collection.JavaConversions._

/**
  * Created by mikhail on 02.02.2016.
  */
class MishanyaGAScheme[T <: Task, N <: Node](factory: MishanyaScheduleCandidateFactory[T, N],
                                             mutation: MishanyaScheduleMutationOperator[T, N],
                                             crossover: MishanyaScheduleCrossoverOperator,
                                             fitnessEvaluator: MishanyaScheduleFitnessEvaluator[T, N],
                                             selector: SelectionStrategy[Object],
                                             rng: Random, popSize: Int) {

  var _best: MishanyaSolution = null

  def evolve(populationSize: Int, eliteCount: Int, seedCandidates: util.Collection[MishanyaSolution], condition: TerminationCondition): MishanyaSolution = {
    evolvePopulation(populationSize, eliteCount, seedCandidates, condition).get(0).getCandidate
  }

  def evolvePopulation(populationSize: Int, eliteCount: Int, seedCandidates: util.Collection[MishanyaSolution], conditions: TerminationCondition): util.List[EvaluatedCandidate[MishanyaSolution]] = {
    if(eliteCount >= 0 && eliteCount < populationSize) {
      if(conditions == null) {
        throw new IllegalArgumentException("At least one TerminationCondition must be specified.")
      } else {
        var currentGenerationIndex: Int = 0
        val startTime: Long = System.currentTimeMillis()
        val population = factory.generateInitialPopulation(populationSize, seedCandidates, rng)
        var evaluatedPopulation = evaluate(population)
        EvolutionUtils.sortEvaluatedPopulation(evaluatedPopulation, fitnessEvaluator.isNatural)
        var data: PopulationData[MishanyaSolution] = EvolutionUtils.getPopulationData(evaluatedPopulation, fitnessEvaluator.isNatural, eliteCount, currentGenerationIndex, startTime)

        while(!conditions.shouldTerminate(data)) {
          currentGenerationIndex += 1
          evaluatedPopulation = nextEvolutionStep(evaluatedPopulation, eliteCount, rng)
          data = EvolutionUtils.getPopulationData(evaluatedPopulation, fitnessEvaluator.isNatural, eliteCount, currentGenerationIndex, startTime)

          println(s"Generation ${data.getGenerationNumber}: ${_best.fitness}")
        }

//        this.satisfiedTerminationConditions = satisfiedConditions;
        EvolutionUtils.sortEvaluatedPopulation(evaluatedPopulation, fitnessEvaluator.isNatural)
        evaluatedPopulation
      }
    } else {
      throw new IllegalArgumentException("Elite count must be non-negative and less than population size.");
    }
  }


  def nextEvolutionStep(evaluatedPopulation: util.List[EvaluatedCandidate[MishanyaSolution]], eliteCount: Int, rng: Random): util.List[EvaluatedCandidate[MishanyaSolution]] = {
    val population :util.ArrayList[MishanyaSolution] = new util.ArrayList[MishanyaSolution]()
    EvolutionUtils.sortEvaluatedPopulation(evaluatedPopulation, fitnessEvaluator.isNatural)
    val locBest = evaluatedPopulation.get(0)
    if (_best == null || _best.fitness > locBest.getCandidate.fitness) {
      _best = locBest.getCandidate.copy
    }

    population.addAll(selector.select(evaluatedPopulation, fitnessEvaluator.isNatural, popSize, rng))
    val children: util.List[MishanyaSolution] = crossover.apply(population, rng)
    val population1: util.List[MishanyaSolution] = mutation.apply(population, rng)
    population1.addAll(children)
    val evPop = evaluate(population1)
    evPop.add(new EvaluatedCandidate[MishanyaSolution](_best.copy, _best.fitness))
    EvolutionUtils.sortEvaluatedPopulation(evPop, fitnessEvaluator.isNatural)
    evPop
  }

  def evaluate(pop: util.List[MishanyaSolution]): util.List[EvaluatedCandidate[MishanyaSolution]] = {
    val res = new util.ArrayList[EvaluatedCandidate[MishanyaSolution]]()
    for (p <- pop) {
      if (p.evaluated) {
        res.add(new EvaluatedCandidate[MishanyaSolution](p, p.fitness))
      } else {
        val fit = fitnessEvaluator.getFitness(p, null)
        p.fitness = fit
        p.evaluated = true
        res.add(new EvaluatedCandidate[MishanyaSolution](p, fit))
      }
    }
    res
  }




}
