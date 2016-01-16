package itmo.escience.simenv.algorithms.ga

import itmo.escience.simenv.algorithms.Scheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.SingleAppWorkload
import itmo.escience.simenv.environment.modelling.Environment
import org.uma.jmetal.algorithm.Algorithm
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection
import org.uma.jmetal.util.{AlgorithmRunner, JMetalLogger}
import scala.collection.JavaConversions._

/**
 * Created by user on 02.12.2015.
 */
class GAScheduler(crossoverProb:Double, mutationProb: Double, swapMutationProb: Double,
                   popSize:Int, iterationCount: Int) extends Scheduler[DaxTask, Node]{

  def coevSchedule(context: Context[DaxTask, Node], environment: Environment[Node], schedPop: List[WorkflowSchedulingSolution]): (Schedule, List[WorkflowSchedulingSolution]) = {

    if (!context.workload.isInstanceOf[SingleAppWorkload]) {
      throw new UnsupportedOperationException(s"Invalid workload type ${context.workload.getClass}. " +
        s"Currently only SingleAppWorkload is supported")
    }

    val wf = context.workload.asInstanceOf[SingleAppWorkload].app
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

  override def schedule(context: Context[DaxTask, Node], environment: Environment[Node]): Schedule = {

    if (!context.workload.isInstanceOf[SingleAppWorkload]) {
      throw new UnsupportedOperationException(s"Invalid workload type ${context.workload.getClass}. " +
        s"Currently only SingleAppWorkload is supported")
    }

    val wf = context.workload.asInstanceOf[SingleAppWorkload].app
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

}
