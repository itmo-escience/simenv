package itmo.escience.simenv.algorithms.wm

import java.util
import java.util.Collections

import org.uma.jmetal.algorithm.Algorithm
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.{GenerationalGeneticAlgorithm, GeneticAlgorithmBuilder, SteadyStateGeneticAlgorithm}
import org.uma.jmetal.operator.{CrossoverOperator, MutationOperator, SelectionOperator}
import org.uma.jmetal.problem.Problem
import org.uma.jmetal.solution.Solution
import org.uma.jmetal.util.JMetalException
import org.uma.jmetal.util.comparator.ObjectiveComparator
import org.uma.jmetal.util.evaluator.SolutionListEvaluator

import scala.collection.JavaConversions._

/**
 * Created by user on 02.12.2015.
 */
class ExtGeneticAlgorithmBuilder[T <: Solution[_]](problem:Problem[T],
                                       crossoverOperator:CrossoverOperator[T],
                                       mutationOperator:MutationOperator[T],
                                                  population: List[T])
  extends GeneticAlgorithmBuilder[T](problem, crossoverOperator, mutationOperator){
  override def build(): Algorithm[T] = {
    if(getVariant == GeneticAlgorithmBuilder.GeneticAlgorithmVariant.GENERATIONAL) {
      new ExtGenerationalGeneticAlgorithm(this.problem, getMaxEvaluations, getPopulationSize,
        this.crossoverOperator, this.mutationOperator, getSelectionOperator, getEvaluator, population)
    } else if(getVariant == GeneticAlgorithmBuilder.GeneticAlgorithmVariant.STEADY_STATE) {
      new ExtSteadyStateGeneticAlgorithm(this.problem, getMaxEvaluations, getPopulationSize,
        this.crossoverOperator, this.mutationOperator, getSelectionOperator)
    } else {
      throw new JMetalException("Unknown variant: " + getVariant)
    }
  }
}

private class ExtGenerationalGeneticAlgorithm[T <: Solution[_]](problem:Problem[T],
                                                                maxEvaluations:Int,
                                                                populationSize:Int,
                                                                crossoverOperator:CrossoverOperator[T],
                                                                mutationOperator:MutationOperator[T],
                                                                selectionOperator:SelectionOperator[java.util.List[T], T],
                                                                evaluator:SolutionListEvaluator[T],
                                                                population: List[T])

  extends GenerationalGeneticAlgorithm[T](problem,maxEvaluations, populationSize,crossoverOperator,
    mutationOperator,
    selectionOperator,
    evaluator){

  val comparator = new ObjectiveComparator[T](0);
  override def updateProgress(): Unit = {
    super.updateProgress()

    val pop = new util.ArrayList(getPopulation)
    Collections.sort(pop, comparator)
    val makespan = pop.head.getObjective(0)

//    println(s"Makespan - ${makespan}")
  }


  override def createInitialPopulation() = {
    if (population == null)
    {
      super.createInitialPopulation()
    } else {
      val res = new util.ArrayList[T]()
      for (p <- population) {
        res.add(p)
      }
      res
    }
  }
}

private class ExtSteadyStateGeneticAlgorithm[T <: Solution[_]](problem:Problem[T],
                                                                maxEvaluations:Int,
                                                                populationSize:Int,
                                                                crossoverOperator:CrossoverOperator[T],
                                                                mutationOperator:MutationOperator[T],
                                                                selectionOperator:SelectionOperator[java.util.List[T], T])

  extends SteadyStateGeneticAlgorithm[T](problem,maxEvaluations, populationSize,crossoverOperator,
    mutationOperator,
    selectionOperator)

