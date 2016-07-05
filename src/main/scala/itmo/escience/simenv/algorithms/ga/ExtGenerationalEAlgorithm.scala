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
  def evolve1(populationSize: Int, eliteCount: Int, seedCandidates: util.Collection[WFSchedSolution], conditions: TerminationCondition): WFSchedSolution = {
    evolvePopulation1(populationSize, eliteCount, seedCandidates, conditions).get(0).getCandidate
  }

  def evolvePopulation1(populationSize: Int, eliteCount: Int, seedCandidates: util.Collection[WFSchedSolution], conditions: TerminationCondition): util.List[EvaluatedCandidate[WFSchedSolution]] = {
    if(eliteCount >= 0 && eliteCount < populationSize) {
      if(conditions == null) {
        throw new IllegalArgumentException("At least one TerminationCondition must be specified.")
      } else {
        //        this.satisfiedTerminationConditions = null
        var currentGenerationIndex: Int = 0
        val startTime: Long = System.currentTimeMillis()
        val population = factory.generateInitialPopulation(populationSize, seedCandidates, rng)
        var evaluatedPopulation = evaluate(population)
        EvolutionUtils.sortEvaluatedPopulation(evaluatedPopulation, fitnessEvaluator.isNatural)
        var data: PopulationData[WFSchedSolution] = EvolutionUtils.getPopulationData(evaluatedPopulation, fitnessEvaluator.isNatural, eliteCount, currentGenerationIndex, startTime)
        val best = data.getBestCandidate
        println(s"Generation ${data.getGenerationNumber}: ${best.fitness}")

        while(!conditions.shouldTerminate(data)) {
          currentGenerationIndex += 1
          evaluatedPopulation = nextEvolutionStep(evaluatedPopulation, eliteCount, rng)
          //          EvolutionUtils.sortEvaluatedPopulation(evaluatedPopulation, this.fitnessEvaluator.isNatural)
          data = EvolutionUtils.getPopulationData(evaluatedPopulation, fitnessEvaluator.isNatural, eliteCount, currentGenerationIndex, startTime)

          val best = data.getBestCandidate
          println(s"Generation ${data.getGenerationNumber}: ${best.fitness}")
          //          this.notifyPopulationChange(data);
        }

        //        this.satisfiedTerminationConditions = satisfiedConditions;
        EvolutionUtils.sortEvaluatedPopulation(evaluatedPopulation, fitnessEvaluator.isNatural)
        evaluatedPopulation
      }
    } else {
      throw new IllegalArgumentException("Elite count must be non-negative and less than population size.");
    }
  }


  override def nextEvolutionStep(evaluatedPopulation: util.List[EvaluatedCandidate[WFSchedSolution]], eliteCount: Int, rng: Random): util.List[EvaluatedCandidate[WFSchedSolution]] = {
    val population :util.ArrayList[WFSchedSolution] = new util.ArrayList[WFSchedSolution](evaluatedPopulation.size())
    val elite: util.ArrayList[WFSchedSolution] = new util.ArrayList[WFSchedSolution](eliteCount)
    EvolutionUtils.sortEvaluatedPopulation(evaluatedPopulation, fitnessEvaluator.isNatural)
    val iterator: util.Iterator[EvaluatedCandidate[WFSchedSolution]] = evaluatedPopulation.iterator()

    val nextEvaluatedPopulation: util.List[EvaluatedCandidate[WFSchedSolution]] = new  util.ArrayList[EvaluatedCandidate[WFSchedSolution]]()

    while(elite.size() < eliteCount) {
      val item = iterator.next
      elite.add(item.getCandidate.copy)
      nextEvaluatedPopulation.add(item)
    }
    //    println(elite.map(x => x.fitness).toList)

    while (iterator.hasNext) {
      nextEvaluatedPopulation.add(iterator.next)
    }

    population.addAll(selector.select(nextEvaluatedPopulation, fitnessEvaluator.isNatural, popSize - eliteCount, rng))
    val population1: util.List[WFSchedSolution] = pipeline.apply(population, rng)
    //    population1.addAll(elite)
    val evPop = evaluate(population1)
    evPop.addAll(elite.map(x => new EvaluatedCandidate[WFSchedSolution](x, x.fitness)))
    EvolutionUtils.sortEvaluatedPopulation(evPop, fitnessEvaluator.isNatural)
    evPop
  }

  def evaluate(pop: util.List[WFSchedSolution]): util.List[EvaluatedCandidate[WFSchedSolution]] = {
    val res = new util.ArrayList[EvaluatedCandidate[WFSchedSolution]]()
    for (p <- pop) {
      val fit = fitnessEvaluator.getFitness(p, null)
      p.fitness = fit
      res.add(new EvaluatedCandidate[WFSchedSolution](p, fit))
    }
    res
  }
}
