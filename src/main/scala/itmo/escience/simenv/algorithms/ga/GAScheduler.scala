package itmo.escience.simenv.algorithms.ga

import itmo.escience.simenv.algorithms.Scheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.SingleAppWorkload
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection
import org.uma.jmetal.solution.DoubleSolution
import org.uma.jmetal.util.{JMetalLogger, AlgorithmRunner}

/**
 * Created by user on 02.12.2015.
 */
object GAScheduler extends Scheduler[DaxTask, CapacityBasedNode]{
  override def schedule(context: Context[DaxTask, CapacityBasedNode]): Schedule = {

    val wf = context.workload.asInstanceOf[SingleAppWorkload].app
    val newSchedule = Schedule.emptySchedule()
    val nodes = context.environment.nodes.filter(x => x.status == Node.UP)


    val problemName = "WorkflowScheduling"

    val crossoverProbability = 0.4
    val mutationProbability = 0.2
    val swapMutationProbability = 0.3

    val crossover = new WorkflowSchedulingCrossover(crossoverProbability)
    val mutation = new WorkflowSchedulingMutation(mutationProbability, swapMutationProbability)
    val selection = new BinaryTournamentSelection[WorkflowSchedulingSolution]()

    val problem = new WorkflowSchedulingProblem(wf, newSchedule, nodes, context)

    val algorithm = new GeneticAlgorithmBuilder[WorkflowSchedulingSolution](problem, crossover, mutation)
      .setSelectionOperator(selection)
      .setMaxEvaluations(100)
      .setPopulationSize(50)
      .build()

    val algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute()

    val best: WorkflowSchedulingSolution = algorithm.getResult
    val computingTime = algorithmRunner.getComputingTime
    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms")


    throw new NotImplementedError()
  }

}
