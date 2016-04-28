package itmo.escience.simenv.utilities.wfGenAlg

import java.util
import java.util.Random

import org.uncommons.maths.random.MersenneTwisterRNG
import org.uncommons.watchmaker.framework._
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection
import org.uncommons.watchmaker.framework.termination.GenerationCount
import scala.collection.JavaConversions._

/**
  * Created by Mishanya on 22.01.2016.
  */

class WfGeneratorGA(crossoverProb:Double, mutationProb: Double,
                    popSize:Int, iterationCount: Int) {


  def run(size: Int, left: Int, right: Int): Unit = {
    val factory: WfCandidateFactory = new WfCandidateFactory(size, left, right)

    val operators: util.List[EvolutionaryOperator[ArrSolution]] = new util.LinkedList[EvolutionaryOperator[ArrSolution]]()
    operators.add(new WfCrossoverOperator(crossoverProb))
    operators.add(new WfMutationOperator(mutationProb, left, right))

    val pipeline: EvolutionaryOperator[ArrSolution] = new EvolutionPipeline[ArrSolution](operators)

    val fitnessEvaluator: FitnessEvaluator[ArrSolution] = new WfFitnessEvaluator()

    val selector: SelectionStrategy[Object] = new RouletteWheelSelection()

    val rng: Random = new MersenneTwisterRNG()

    val  engine: EvolutionEngine[ArrSolution] = new ExtGenerationalEAlgorithm(factory,
      pipeline,
      fitnessEvaluator,
      selector,
      rng, popSize)

    if (true) {
      engine.addEvolutionObserver(new EvolutionObserver[ArrSolution]() {
        def populationUpdate(data: PopulationData[_ <: ArrSolution]) = {
          val best = data.getBestCandidate
          println("------------------")
          println(s"Generation ${data.getGenerationNumber}: best = ${best.arr.toList}; fit = ${best.fitness}")
          println(s"Mean fit: ${data.getMeanFitness} Std: ${data.getFitnessStandardDeviation}\n")
        }
      })
    }

    val seeds: util.ArrayList[ArrSolution] = new util.ArrayList[ArrSolution]()

    val result = engine.evolve(popSize, 1, seeds, new GenerationCount(iterationCount))
    println(s"\n--Best = ${result.arr.toList}; fit = ${result.fitness}--")
  }

}
