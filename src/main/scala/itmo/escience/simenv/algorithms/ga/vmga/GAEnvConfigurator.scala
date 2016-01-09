package itmo.escience.simenv.algorithms.ga.vmga

import itmo.escience.simenv.algorithms.Scheduler
import itmo.escience.simenv.algorithms.ga.ExtGeneticAlgorithmBuilder
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.{PhysResourceEnvironment, SingleAppWorkload}
import org.uma.jmetal.algorithm.Algorithm
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection
import org.uma.jmetal.util.{AlgorithmRunner, JMetalLogger}

/**
 * Created by user on 02.12.2015.
 */
class GAEnvConfigurator(crossoverProb:Double, mutationProb: Double,
                        popSize:Int, iterationCount: Int) {

  def environmentConfig(context: Context[DaxTask, CoreRamHddBasedNode], schedule: Schedule): PhysResourceEnvironment = {

    if (!context.workload.isInstanceOf[SingleAppWorkload]) {
      throw new UnsupportedOperationException(s"Invalid workload type ${context.workload.getClass}. " +
        s"Currently only SingleAppWorkload is supported")
    }

    val wf = context.workload.asInstanceOf[SingleAppWorkload].app
    val nodes = context.environment.nodes.filter(x => x.status == Node.UP)


    val problemName = "EnvironmentConfiguration"
    val problem = new EnvConfigurationProblem(wf, schedule, nodes, context)

    val crossover = new EnvConfigurtionCrossover(crossoverProb, context)
    val mutation = new EnvConfigurationMutation(mutationProb, context)
    val selection = new BinaryTournamentSelection[EnvConfigurationSolution]()

    val algorithm: Algorithm[EnvConfigurationSolution] =
      new ExtGeneticAlgorithmBuilder[EnvConfigurationSolution](problem, crossover, mutation)
      .setSelectionOperator(selection)
      .setMaxEvaluations(iterationCount * popSize)
      .setPopulationSize(popSize)
      .build()

    val algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute()

    val best: EnvConfigurationSolution = algorithm.getResult
    val computingTime = algorithmRunner.getComputingTime
    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms")

    EnvConfigurationProblem.solutionToEnvironment(best, context)
  }

}
