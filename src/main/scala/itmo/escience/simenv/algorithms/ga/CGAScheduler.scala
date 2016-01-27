package itmo.escience.simenv.algorithms.ga

import java.util
import java.util.Random

import itmo.escience.simenv.algorithms.Scheduler
import itmo.escience.simenv.algorithms.ga.env.{EnvCrossoverOperator, EnvMutationOperator, EnvConfSolution, EnvCandidateFactory}
import itmo.escience.simenv.algorithms.vm.env.EnvConfigurationProblem
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.BasicEnvironment
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.maths.random.MersenneTwisterRNG
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection
import org.uncommons.watchmaker.framework._
import org.uncommons.watchmaker.framework.termination.GenerationCount

/**
  * Created by mikhail on 25.01.2016.
  */
class CGAScheduler (crossoverProb:Double, mutationProb: Double, swapMutationProb: Double,
                    popSize:Int, iterationCount: Int) extends Scheduler{
  override def schedule[T <: Task, N <: Node](context: Context[T, N], environment: Environment[N]): Schedule[T, N] = {
    val schedFactory: ScheduleCandidateFactory[T, N] = new ScheduleCandidateFactory[T, N](context, environment)
    val envFactory: EnvCandidateFactory[T, N] = new EnvCandidateFactory[T, N](context, environment, environment.asInstanceOf[BasicEnvironment].getTypes)

    val schedOperators: util.List[EvolutionaryOperator[WFSchedSolution]] = new util.LinkedList[EvolutionaryOperator[WFSchedSolution]]()
    schedOperators.add(new ScheduleCrossoverOperator())
    schedOperators.add(new ScheduleMutationOperator[T, N](context, environment, mutationProb, swapMutationProb))

    val schedPipeline: EvolutionaryOperator[WFSchedSolution] = new EvolutionPipeline[WFSchedSolution](schedOperators)

    val envOperators: util.List[EvolutionaryOperator[EnvConfSolution]] = new util.LinkedList[EvolutionaryOperator[EnvConfSolution]]()
    envOperators.add(new EnvCrossoverOperator())
    envOperators.add(new EnvMutationOperator[T, N](context, environment,
      mutationProb, environment.asInstanceOf[BasicEnvironment].getTypes))


    val envPipeline: EvolutionaryOperator[EnvConfSolution] = new EvolutionPipeline[EnvConfSolution](envOperators)

    val fitnessEvaluator: ScheduleFitnessEvaluator[T, N] = new ScheduleFitnessEvaluator[T, N](context, environment)

    val selector: SelectionStrategy[Object] = new RouletteWheelSelection()

    val rng: Random = new MersenneTwisterRNG()

    val  engine: CoevolutionGenerationalEvolutionEngine[T, N] = new CoevolutionGenerationalEvolutionEngine[T, N](schedFactory=schedFactory,
      envFactory=envFactory, schedOperators=schedPipeline, envOperators=envPipeline,
      fitnessEvaluator=fitnessEvaluator,
      selector,
      rng)

    engine.addEvolutionObserver(new EvolutionObserver[WFSchedSolution]()
    {
      def populationUpdate(data :PopulationData[_ <: WFSchedSolution]) =
      {
        val best = data.getBestCandidate
        val bestMakespan = WorkflowSchedulingProblem.solutionToSchedule(best, context, environment).makespan()
        println(s"Generation ${data.getGenerationNumber}: $bestMakespan\n")
      }
    })

    val result: (WFSchedSolution, EnvConfSolution, Double) = engine.evolve(popSize, 1, new GenerationCount(iterationCount))
    val newEnv = EnvConfigurationProblem.solutionToEnvironment[T, N](result._2, context)
    println(newEnv.envPrint())
    WorkflowSchedulingProblem.solutionToSchedule(result._1, context, newEnv)
  }
}
