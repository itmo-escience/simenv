package itmo.escience.simenv

import java.util
import java.util.Random

import itmo.escience.simenv.entities.{CpuRamNode, DaxTask}
import itmo.escience.simenv.entities.CarrierNodeEnvironment
import itmo.escience.simenv.ga._
import itmo.escience.simenv.utilities.{StormScheduleVisualizer, JSONParser}
import org.uncommons.maths.random.MersenneTwisterRNG
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection
import org.uncommons.watchmaker.framework._
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline
import org.uncommons.watchmaker.framework.termination.GenerationCount
import scala.collection.JavaConversions._

/**
  * Created by Mishanya on 23.12.2015.
  */
class StormScheduler(workloadPath: String, envPath: String, globNet: Int, localNet: Int, initSol: List[String] = null) {

  var env: CarrierNodeEnvironment[CpuRamNode] = null
  var tasks: util.HashMap[String, DaxTask] = null

  var scheduler: EvolutionEngine[SSSolution] = null

  var seeds: util.ArrayList[SSSolution] = new util.ArrayList[SSSolution]()
  val seedMap: util.HashMap[String, SSSolution] = new util.HashMap[String, SSSolution]()

  var fitnessEvaluator: ScheduleFitnessEvaluator = null

  val rnd: Random = new Random()

  // GA params
  val crossProb = 0.5
  val mutProb = 0.3
  val popSize = 50
  val iterations = 100

  // Объект визуализатора.
    var vis: StormScheduleVisualizer = null

  def initialization(): Unit = {
    // Из JSON
    env = JSONParser.parseEnv(envPath, globNet, localNet)
    tasks = JSONParser.parseWorkload(workloadPath)
    if (initSol != null) {
      for (s <- initSol) {
        val sol: SSSolution = JSONParser.parseSolution(s)
        seeds.add(sol)
        seedMap.put(s, sol)
      }
    }

    val factory: ScheduleCandidateFactory = new ScheduleCandidateFactory(env, tasks)

    val operators: util.List[EvolutionaryOperator[SSSolution]] = new util.LinkedList[EvolutionaryOperator[SSSolution]]()
    operators.add(new ScheduleCrossoverOperator(env, tasks, crossProb))
    operators.add(new ScheduleMutationOperator(env, tasks, mutProb))

    val pipeline: EvolutionaryOperator[SSSolution] = new EvolutionPipeline[SSSolution](operators)

    fitnessEvaluator = new ScheduleFitnessEvaluator(env, tasks)

    val selector: SelectionStrategy[Object] = new RouletteWheelSelection()

    val rng: Random = new MersenneTwisterRNG()

    scheduler = new ExtGenerationalEAlgorithm(factory,
      pipeline,
      fitnessEvaluator,
      selector,
      rng, popSize)



     vis = new StormScheduleVisualizer(env, tasks)

    println("Initialization complete")
  }

  var resFitness: Double = 0.0
  def run(needPrint: Boolean = true): java.util.HashMap[String, List[String]] = {
//    val factory = new ScheduleCandidateFactory(env, tasks)
//    val result = factory.generateRandomCandidate(rnd)
//
//    // Seed estimation
    if (needPrint) {
      for (s <- seedMap.keySet()) {
        println("seed")
        println(s)
        println(seedMap.get(s).genes.toString)
        println(fitnessEvaluator.getFitness(seedMap.get(s), null))
      }
      println("----")
    }

//    val seedFitness = fitnessEvaluator.getFitness(seeds.get(0))
//    println("Fitness of init solution: " + seedFitness)
    if (needPrint) {
      scheduler.addEvolutionObserver(new EvolutionObserver[SSSolution]() {
        def populationUpdate(data: PopulationData[_ <: SSSolution]) = {
          println(s"Generation ${data.getGenerationNumber}: ${data.getBestCandidateFitness}\n")
        }
      })
    }

//    val result = scheduler.evolve(popSize, 1, new util.ArrayList[SSSolution](), new GenerationCount(iterations))
    val result = scheduler.evolve(popSize, 1, seeds, new GenerationCount(iterations))
    println(s"result: ${fitnessEvaluator.getFitness(result)}\n" + StormSchedulingProblem.mapToString(result.genes))
    resFitness = fitnessEvaluator.getFitness(result)
    println(result.genes.toString)
    val schedule = StormSchedulingProblem.solutionToSchedule(result)
    schedule
  }

  def runFit(): Double = {
//    if (resFitness == null) {
//      throw new IllegalStateException("Exception thrown");
//    }
    resFitness
  }


  def drawSolution(solution: SSSolution) = {
    vis.drawSched(solution)
  }

//  def scheduleToJSON(schedule: java.util.HashMap[String, List[(String, Double)]]): String = {
//    JSONParser.scheduleToJSON(schedule)
//  }

  def scheduleToMapList(schedule: java.util.HashMap[String, List[String]]): java.util.HashMap[String, java.util.ArrayList[java.util.ArrayList[Object]]] = {
    val result = new java.util.HashMap[String, java.util.ArrayList[java.util.ArrayList[Object]]]()
    for (k <- schedule.keySet()) {
      val list = schedule.get(k)
      val outList = new java.util.ArrayList[java.util.ArrayList[Object]]()
      for (l <- list) {
        val inList = new java.util.ArrayList[Object]()
        inList.add(l.asInstanceOf[Object])
//        inList.add(l._2.asInstanceOf[Object])
        outList.add(inList)
      }
      result.put(k, outList)
    }
    result
  }


  def scheduleToMap(schedule: java.util.HashMap[String, List[String]]): java.util.HashMap[String, java.util.ArrayList[String]] = {
    val result = new java.util.HashMap[String, java.util.ArrayList[String]]()
    for (k <- schedule.keySet()) {
      val list = schedule.get(k)
      val outList = new java.util.ArrayList[String]()
      for (l <- list) {
        outList.add(l)
      }
      result.put(k, outList)
    }
    result
  }

}