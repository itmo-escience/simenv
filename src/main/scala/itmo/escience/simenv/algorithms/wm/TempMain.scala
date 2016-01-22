package itmo.escience.simenv.algorithms.wm

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder
import org.uma.jmetal.operator.impl.crossover.SBXCrossover
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection
import org.uma.jmetal.runner.AbstractAlgorithmRunner
import org.uma.jmetal.solution.DoubleSolution
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator
import org.uma.jmetal.util.{AlgorithmRunner, JMetalLogger, ProblemUtils}

/**
 * Created by user on 02.12.2015.
 */
object TempMain extends AbstractAlgorithmRunner{

  def main(args: Array[String])= {

    val problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1"
    val referenceParetoFront = "D:\\wspace\\simenv\\resources\\ga\\ZDT1.pf"
    val problem = ProblemUtils.loadProblem[DoubleSolution] (problemName)

    val crossoverProbability = 0.9
    val crossoverDistributionIndex = 20.0
    val crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex)

    val mutationProbability = 1.0 / problem.getNumberOfVariables
    val mutationDistributionIndex = 20.0
    val mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex)

    val selection = new BinaryTournamentSelection[DoubleSolution](new RankingAndCrowdingDistanceComparator[DoubleSolution]())

    val algorithm = new NSGAIIBuilder[DoubleSolution](problem, crossover, mutation)
      .setSelectionOperator(selection)
      .setMaxIterations(50000)
      .setPopulationSize(100)
      .build()

    val algorithmRunner = new AlgorithmRunner.Executor(algorithm)
      .execute() ;

    val population: java.util.List[DoubleSolution] = algorithm.getResult
    val computingTime = algorithmRunner.getComputingTime

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms")

    AbstractAlgorithmRunner.printFinalSolutionSet(population)
    if (!referenceParetoFront.equals("")) {
      AbstractAlgorithmRunner.printQualityIndicators(population, referenceParetoFront)
    }
  }

}
