package itmo.escience.simenv.algorithms.ga

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder
import org.uma.jmetal.operator.impl.crossover.SBXCrossover
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection
import org.uma.jmetal.runner.AbstractAlgorithmRunner
import org.uma.jmetal.solution.DoubleSolution
import org.uma.jmetal.util.{JMetalLogger, AlgorithmRunner, ProblemUtils}
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator

/**
 * Created by user on 02.12.2015.
 */
object TempMain_ga extends AbstractAlgorithmRunner{

  def main(args: Array[String])= {

//    val problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1"
//    val referenceParetoFront = "D:\\wspace\\simenv\\resources\\ga\\ZDT1.pf"
//    val problem = ProblemUtils.loadProblem[DoubleSolution] (problemName)

    val problemName = "WorkflowScheduling"

    val crossoverProbability = 0.4
    val mutationProbability = 0.2
    val swapMutationProbability = 0.3

    val crossover = new WorkflowSchedulingCrossover(crossoverProbability)
    val mutation = new WorkflowSchedulingMutation(mutationProbability, swapMutationProbability)
    val selection = new BinaryTournamentSelection[WorkflowSchedulingSolution]()

    val problem = new WorkflowSchedulingProblem()

    val algorithm = new NSGAIIBuilder[DoubleSolution](problem, crossover, mutation)
          .setSelectionOperator(selection)
          .setMaxIterations(50000)
          .setPopulationSize(100)
          .build()


//    val crossoverProbability = 0.9
//    val crossoverDistributionIndex = 20.0
//    val crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex)
//
//    val mutationProbability = 1.0 / problem.getNumberOfVariables
//    val mutationDistributionIndex = 20.0
//    val mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex)

//    val selection = new BinaryTournamentSelection[DoubleSolution](new RankingAndCrowdingDistanceComparator[DoubleSolution]())

//    val algorithm = new NSGAIIBuilder[DoubleSolution](problem, crossover, mutation)
//      .setSelectionOperator(selection)
//      .setMaxIterations(50000)
//      .setPopulationSize(100)
//      .build()
//    val algorithmRunner = new AlgorithmRunner.Executor(algorithm)
//      .execute() ;
//
//    val population: java.util.List[DoubleSolution] = algorithm.getResult
//    val computingTime = algorithmRunner.getComputingTime
//
//    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms")
//
//    AbstractAlgorithmRunner.printFinalSolutionSet(population)
//    if (!referenceParetoFront.equals("")) {
//      AbstractAlgorithmRunner.printQualityIndicators(population, referenceParetoFront)
//    }
  }

}
