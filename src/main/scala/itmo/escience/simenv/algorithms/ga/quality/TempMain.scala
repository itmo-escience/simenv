package itmo.escience.simenv.algorithms.ga.quality

import java.util.Random

import itmo.escience.simenv.algorithms.ga.WorkflowSchedulingProblem
import itmo.escience.simenv.environment.entities.{DaxTask, CpuTimeNode, Context}
import itmo.escience.simenv.environment.modelling.Environment

/**
  * Created by user on 17.02.2016.
  */
object TempMain {

  val populationSize = 50
  val eliteCount = 10

  val context:Context[DaxTask, CpuTimeNode] = null
  val environment:Environment[CpuTimeNode] = null

  def main(args: Array[String]):Unit = {

    throw new NotImplementedError()

    val engine = new QBGenerationalEvolutionEngine(new QBIndividualsGenerator(),
      new QBEvolutionScheme(new QBCrossover, new QBMutation),
      new QBFitnessEvaluator(),
      new QBSelectionStrategy(),
      new Random())


    val bestSol = engine.evolve(populationSize, eliteCount, LimitedGenerations(100))

    val schedule = QBFitnessEvaluator.solutionToSchedule(bestSol, context, environment)
    println(s"Schedule makespan is ${schedule.makespan()}")
  }

}
