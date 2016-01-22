package itmo.escience.simenv.algorithms.wm

import java.util.Random

import itmo.escience.simenv.algorithms.Scheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.SingleAppWorkload
import itmo.escience.simenv.environment.modelling.Environment
import org.uma.jmetal.algorithm.Algorithm
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection
import org.uma.jmetal.util.{AlgorithmRunner, JMetalLogger}
import org.uncommons.maths.random.MersenneTwisterRNG
import org.uncommons.watchmaker.framework.factories.StringFactory
import org.uncommons.watchmaker.framework.{CandidateFactory, GenerationalEvolutionEngine, EvolutionEngine}

import scala.collection.JavaConversions._

/**
 * Created by user on 02.12.2015.
 */
class GAScheduler[N <: Node](crossoverProb:Double, mutationProb: Double, swapMutationProb: Double,
                   popSize:Int, iterationCount: Int) extends Scheduler[DaxTask, N]{

  def coevSchedule(context: Context[DaxTask, N], environment: Environment[N], schedPop: List[WorkflowSchedulingSolution]): (Schedule, List[WorkflowSchedulingSolution]) = {

    if (!context.workload.isInstanceOf[SingleAppWorkload[DaxTask]]) {
      throw new UnsupportedOperationException(s"Invalid workload type ${context.workload.getClass}. " +
        s"Currently only SingleAppWorkload is supported")
    }

    val wf = context.workload.asInstanceOf[SingleAppWorkload[DaxTask]].app
    val newSchedule = Schedule.emptySchedule()
    val nodes = context.environment.nodes.filter(x => x.status == NodeStatus.UP)


    val problemName = "WorkflowScheduling"
    val problem = new WorkflowSchedulingProblem(wf, newSchedule, context, environment)

    val crossover = new WorkflowSchedulingCrossover(crossoverProb)
    val mutation = new WorkflowSchedulingMutation(mutationProb, swapMutationProb, context)
    val selection = new BinaryTournamentSelection[WorkflowSchedulingSolution]()

    val algorithm: Algorithm[WorkflowSchedulingSolution] =
      new ExtGeneticAlgorithmBuilder[WorkflowSchedulingSolution](problem, crossover, mutation, schedPop)
      .setSelectionOperator(selection)
      .setMaxEvaluations(iterationCount * popSize)
      .setPopulationSize(popSize)
      .build()

    val algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute()

    val best: WorkflowSchedulingSolution = algorithm.getResult

    val pop: List[WorkflowSchedulingSolution] = algorithm.asInstanceOf[ExtGenerationalGeneticAlgorithm[WorkflowSchedulingSolution]].getPopulation.toList

    val computingTime = algorithmRunner.getComputingTime
    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms")

    //throw new NotImplementedError()
    (WorkflowSchedulingProblem.solutionToSchedule(best, context, environment), pop)
  }

  override def schedule(context: Context[DaxTask, N], environment: Environment[N]): Schedule = {

    val wf = context.workload.apps.head
    val newSchedule = Schedule.emptySchedule()
    val nodes = context.environment.nodes.filter(x => x.status == NodeStatus.UP)

    val problemName = "WorkflowScheduling"
    val problem = new WorkflowSchedulingProblem(wf, newSchedule, context, environment)

    val crossover = new WorkflowSchedulingCrossover(crossoverProb)
    val mutation = new WorkflowSchedulingMutation(mutationProb, swapMutationProb, context)
    val selection = new BinaryTournamentSelection[WorkflowSchedulingSolution]()

    val algorithm: Algorithm[WorkflowSchedulingSolution] =
      new ExtGeneticAlgorithmBuilder[WorkflowSchedulingSolution](problem, crossover, mutation, null)
        .setSelectionOperator(selection)
        .setMaxEvaluations(iterationCount * popSize)
        .setPopulationSize(popSize)
        .build()

    val algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute()

    val best: WorkflowSchedulingSolution = algorithm.getResult
    val computingTime = algorithmRunner.getComputingTime
    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms")


    //throw new NotImplementedError()
    WorkflowSchedulingProblem.solutionToSchedule(best, context, environment)
  }

  override def schedule(context: Context[DaxTask, N], environment: Environment[N]): Schedule = {
    val wf = context.workload.apps.head
    val newSchedule = Schedule.emptySchedule()
    val nodes = context.environment.nodes.filter(x => x.status == NodeStatus.UP)


    val factory: CandidateFactory[String] = new StringFactory(chars, 11)

    val rng: Random = new MersenneTwisterRNG()

    val  engine: EvolutionEngine[String] = new GenerationalEvolutionEngine[String](factory,
      pipeline,
      fitnessEvaluator,
      selector,
      rng)
  }

}
