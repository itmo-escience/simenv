package itmo.escience.simenv.algorithms.ga

import java.util
import java.util.Random

import itmo.escience.simenv.algorithms.{MinMinScheduler, HEFTScheduler, Scheduler}
import itmo.escience.simenv.algorithms.ga.env._
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.{CarrierNodeEnvironment, BasicEnvironment}
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.maths.random.MersenneTwisterRNG
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection
import org.uncommons.watchmaker.framework.termination.GenerationCount
import org.uncommons.watchmaker.framework.{PopulationData, EvolutionObserver, SelectionStrategy, EvolutionaryOperator}
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline

/**
  * Created by mikhail on 25.01.2016.
  */
class CGAScheduler (crossoverProb:Double, mutationProb: Double, swapMutationProb: Double,
                    popSize:Int, iterationCount: Int) extends Scheduler{
  override def schedule[T <: Task, N <: Node](context: Context[T, N], environment: Environment[N]): Schedule[T, N] = {
    val schedFactory: ScheduleCandidateFactory[T, N] = new ScheduleCandidateFactory[T, N](context, environment)
    val envFactory: EnvCandidateFactory[T, N] = new EnvCandidateFactory[T, N](environment)

    val schedMut = new ScheduleMutationOperator[T, N](context, environment, mutationProb, swapMutationProb)
    val schedCross = new ScheduleCrossoverOperator()

    val envOperators: util.List[EvolutionaryOperator[EnvConfSolution]] = new util.LinkedList[EvolutionaryOperator[EnvConfSolution]]()
    envOperators.add(new EnvCrossoverOperator(environment))
    envOperators.add(new EnvMutationOperator[N](environment, mutationProb))


    val envPipeline: EvolutionaryOperator[EnvConfSolution] = new EvolutionPipeline[EnvConfSolution](envOperators)

    val fitnessEvaluator: ScheduleFitnessEvaluator[T, N] = new ScheduleFitnessEvaluator[T, N](context, environment)

    val selector: SelectionStrategy[Object] = new RouletteWheelSelection()

    val rng: Random = new MersenneTwisterRNG()

    val  engine: CoevolutionGenerationalEvolutionEngine[T, N] = new CoevolutionGenerationalEvolutionEngine[T, N](schedFactory=schedFactory,
      envFactory=envFactory, schedMutOperator=schedMut, schedCrossOperator=schedCross, envOperators=envPipeline,
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
    seeds.add(WorkflowSchedulingProblem.scheduleToSolution[T, N](heft_schedule, context, environment))
    seeds.add(WorkflowSchedulingProblem.scheduleToSolution[T, N](min_schedule, context, environment))
    seeds.add(EnvConfigurationProblem.environmentToSolution(environment))
    seeds.add(EnvConfigurationProblem.environmentToSolution(environment))

    val result: (WFSchedSolution, EnvConfSolution, Double) = engine.evolve(popSize, 1, seeds, new GenerationCount(iterationCount))
    val newEnv = EnvConfigurationProblem.solutionToEnvironment[T, N](result._2, context)
    println("CGA environment: ")
    println(newEnv.asInstanceOf[CarrierNodeEnvironment[CapacityBasedNode]].envPrint())
    WorkflowSchedulingProblem.solutionToSchedule(result._1, context, newEnv)
  }

  def coevSchedule[T <: Task, N <: Node](context: Context[T, N], environment: Environment[N]): (Schedule[T, N], Environment[N]) = {
    val schedFactory: ScheduleCandidateFactory[T, N] = new ScheduleCandidateFactory[T, N](context, environment)
    val envFactory: EnvCandidateFactory[T, N] = new EnvCandidateFactory[T, N](environment)

    val schedMut = new ScheduleMutationOperator[T, N](context, environment, mutationProb, swapMutationProb)
    val schedCross = new ScheduleCrossoverOperator()

    val envOperators: util.List[EvolutionaryOperator[EnvConfSolution]] = new util.LinkedList[EvolutionaryOperator[EnvConfSolution]]()
    envOperators.add(new EnvCrossoverOperator(environment))
    envOperators.add(new EnvMutationOperator[N](environment, mutationProb))


    val envPipeline: EvolutionaryOperator[EnvConfSolution] = new EvolutionPipeline[EnvConfSolution](envOperators)

    val fitnessEvaluator: ScheduleFitnessEvaluator[T, N] = new ScheduleFitnessEvaluator[T, N](context, environment)

    val selector: SelectionStrategy[Object] = new RouletteWheelSelection()

    val rng: Random = new MersenneTwisterRNG()

    val  engine: CoevolutionGenerationalEvolutionEngine[T, N] = new CoevolutionGenerationalEvolutionEngine[T, N](schedFactory=schedFactory,
      envFactory=envFactory, schedMutOperator=schedMut, schedCrossOperator=schedCross, envOperators=envPipeline,
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
    seeds.add(WorkflowSchedulingProblem.scheduleToSolution[T, N](heft_schedule, context, environment))
    seeds.add(WorkflowSchedulingProblem.scheduleToSolution[T, N](min_schedule, context, environment))
    seeds.add(EnvConfigurationProblem.environmentToSolution(environment))

    val result: (WFSchedSolution, EnvConfSolution, Double) = engine.evolve(popSize, 1, seeds, new GenerationCount(iterationCount))
    val newEnv = EnvConfigurationProblem.solutionToEnvironment[T, N](result._2, context)
    println("CGA environment: ")
    println(newEnv.asInstanceOf[CarrierNodeEnvironment[CapacityBasedNode]].envPrint())
    (WorkflowSchedulingProblem.coevSolutionToSchedule(result._1, context, newEnv), newEnv)
  }
}
