package itmo.escience.simenv

import java.util
import java.util.Random

import itmo.escience.simenv.entities.{CpuRamNode, DaxTask}
import itmo.escience.simenv.entities.CarrierNodeEnvironment
import itmo.escience.simenv.ga._
import itmo.escience.simenv.utilities.JSONParser
import org.uncommons.maths.random.MersenneTwisterRNG
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection
import org.uncommons.watchmaker.framework._
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline
import org.uncommons.watchmaker.framework.termination.GenerationCount

/**
  * Created by Mishanya on 23.12.2015.
  */
class StormScheduler(workloadPath: String, envPath: String, globNet: Int, localNet: Int, initSol: String = null) {

  var env: CarrierNodeEnvironment[CpuRamNode] = null
  var tasks: util.HashMap[String, DaxTask] = null

  var scheduler: EvolutionEngine[SSSolution] = null

  var seeds: util.ArrayList[SSSolution] = new util.ArrayList[SSSolution]()

  val rnd: Random = new Random()

  // GA params
  val crossProb = 0.5
  val mutProb = 0.3
  val popSize = 50
  val iterations = 1000

  // Объект визуализатора.
//    var vis: StormScheduleVisualizer = null

  def initialization(): Unit = {
    // Из JSON
    env = JSONParser.parseEnv(envPath, globNet, globNet)
    tasks = JSONParser.parseWorkload(workloadPath)

    val factory: ScheduleCandidateFactory = new ScheduleCandidateFactory(env, tasks)

    val operators: util.List[EvolutionaryOperator[SSSolution]] = new util.LinkedList[EvolutionaryOperator[SSSolution]]()
    operators.add(new ScheduleCrossoverOperator(env, tasks, crossProb))
    operators.add(new ScheduleMutationOperator(env, tasks, mutProb))

    val pipeline: EvolutionaryOperator[SSSolution] = new EvolutionPipeline[SSSolution](operators)

    val fitnessEvaluator: ScheduleFitnessEvaluator = new ScheduleFitnessEvaluator(env, tasks)

    val selector: SelectionStrategy[Object] = new RouletteWheelSelection()

    val rng: Random = new MersenneTwisterRNG()

    scheduler = new ExtGenerationalEAlgorithm(factory,
      pipeline,
      fitnessEvaluator,
      selector,
      rng, popSize)

    scheduler.addEvolutionObserver(new EvolutionObserver[SSSolution]() {
      def populationUpdate(data: PopulationData[_ <: SSSolution]) = {
        println(s"Generation ${data.getGenerationNumber}: ${data.getBestCandidateFitness}\n")
      }
    })

    // vis = new StormScheduleVisualizer(tasks)

    println("Initialization complete")
  }

  def run(): java.util.HashMap[String, (String, Double)] = {
    val fitnessEvaluator: ScheduleFitnessEvaluator = new ScheduleFitnessEvaluator(env, tasks)
    val result = scheduler.evolve(popSize, 1, seeds, new GenerationCount(iterations))
    println(s"result: ${fitnessEvaluator.getFitness(result)}\n" + StormSchedulingProblem.mapToString(result.genes))
    result.genes
  }

}