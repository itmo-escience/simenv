package itmo.escience.simenv.algorithms.ecgProcessing

import java.util
import java.util.Random

import itmo.escience.simenv.algorithms.ga._
import itmo.escience.simenv.algorithms.ga.env.{EnvConfSolution, EnvCandidateFactory}
import itmo.escience.simenv.algorithms.{HEFTScheduler, MinMinScheduler, Scheduler}
import itmo.escience.simenv.environment.ecgProcessing.CoreStorageNode
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.CarrierNodeEnvironment
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.maths.random.MersenneTwisterRNG
import org.uncommons.watchmaker.framework._
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection
import org.uncommons.watchmaker.framework.termination.GenerationCount

/**
  * Created by Mishanya on 22.01.2016.
  */

class EcgGAScheduler(crossoverProb:Double, mutationProb: Double, swapMutationProb: Double,
                     popSize:Int, iterationCount: Int) extends CGAScheduler(crossoverProb, mutationProb, swapMutationProb, popSize, iterationCount){


  override def schedule[T <: Task, N <: Node](context: Context[T, N], environment: Environment[N]): Schedule[T, N] = {
    null
  }

  override def coevSchedule[T <: Task, N <: Node](context: Context[T, N], environment: Environment[N]): (Schedule[T, N], Environment[N]) = {
    val schedFactory: ScheduleCandidateFactory[T, N] = new ScheduleCandidateFactory[T, N](context, environment)
    val envFactory: EcgEnvCandidateFactory = new EcgEnvCandidateFactory(environment.asInstanceOf[CarrierNodeEnvironment[CoreStorageNode]])

    val schedMut = new ScheduleMutationOperator[T, N](context, environment, mutationProb, swapMutationProb)
    val schedCross = new ScheduleCrossoverOperator(crossoverProb)

    val envOperators: util.List[EvolutionaryOperator[EcgEnvConfSolution]] = new util.LinkedList[EvolutionaryOperator[EcgEnvConfSolution]]()
    envOperators.add(new EcgEnvCrossoverOperator[N](environment, crossoverProb))
    envOperators.add(new EcgEnvMutationOperator(environment.asInstanceOf[CarrierNodeEnvironment[CoreStorageNode]], mutationProb))


    val envPipeline: EvolutionaryOperator[EcgEnvConfSolution] = new EvolutionPipeline[EcgEnvConfSolution](envOperators)

    val fitnessEvaluator: ScheduleFitnessEvaluator[T, N] = new ScheduleFitnessEvaluator[T, N](context, environment)

    val selector: SelectionStrategy[Object] = new RouletteWheelSelection()

    val rng: Random = new MersenneTwisterRNG()

    val  engine: EcgCoevolutionGenerationalEvolutionEngine[T, N] = new EcgCoevolutionGenerationalEvolutionEngine[T, N](schedFactory=schedFactory,
      envFactory=envFactory, schedMutOperator=schedMut, schedCrossOperator=schedCross,
      envOperators=envPipeline,
      fitnessEvaluator=fitnessEvaluator,
      selector,
      rng)

    //    engine.addEvolutionObserver(new EvolutionObserver[WFSchedSolution]()
    //    {
    //      def populationUpdate(data :PopulationData[_ <: WFSchedSolution]) =
    //      {
    //        val best = data.getBestCandidate
    //        val bestMakespan = WorkflowSchedulingProblem.solutionToSchedule(best, context, environment).makespan()
    //        println(s"Generation ${data.getGenerationNumber}: $bestMakespan\n")
    //      }
    //    })


    val heft_schedule = HEFTScheduler.schedule(context, environment)
    val min_schedule = MinMinScheduler.schedule(context, environment)
    val seeds: util.ArrayList[EvSolution[_]] = new util.ArrayList[EvSolution[_]]()
    //    seeds.add(WorkflowSchedulingProblem.scheduleToSolution[T, N](heft_schedule, context, environment))
    //    seeds.add(WorkflowSchedulingProblem.scheduleToSolution[T, N](min_schedule, context, environment))
    //    seeds.add(EnvConfigurationProblem.environmentToSolution(environment))

    val result: (WFSchedSolution, EcgEnvConfSolution, Double) = engine.evolve(popSize, 1, seeds, new GenerationCount(iterationCount))
    val newEnv = EcgEnvConfigurationProblem.solutionToEnvironment(result._2, context.asInstanceOf[Context[DaxTask, CoreStorageNode]])
    //    println("CGA environment: ")
    //    println(newEnv.asInstanceOf[CarrierNodeEnvironment[CapacityBasedNode]].envPrint())
    (WorkflowSchedulingProblem.ecgSolutionToSchedule(result._1, context, newEnv.asInstanceOf[Environment[N]]), newEnv.asInstanceOf[Environment[N]])
  }

}
