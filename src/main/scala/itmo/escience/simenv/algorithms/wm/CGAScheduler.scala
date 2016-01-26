package itmo.escience.simenv.algorithms.wm

import java.util
import java.util.Random

import itmo.escience.simenv.algorithms.Scheduler
import itmo.escience.simenv.algorithms.wm.env.EnvCandidateFactory
import itmo.escience.simenv.environment.entities.{Schedule, Context, Node, Task}
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
    val envFactory: EnvCandidateFactory[T, N] = new EnvCandidateFactory[T, N](context, environment, environment.asInstanceOf[BasicEnvironment].types)

    val operators: util.List[EvolutionaryOperator[WFSchedSolution]] = new util.LinkedList[EvolutionaryOperator[WFSchedSolution]]()
    operators.add(new ScheduleCrossoverOperator())
    operators.add(new ScheduleMutationOperator[T, N](context, environment, mutationProb, swapMutationProb))

    val pipeline: EvolutionaryOperator[WFSchedSolution] = new EvolutionPipeline[WFSchedSolution](operators)

    val fitnessEvaluator: FitnessEvaluator[WFSchedSolution] = new ScheduleFitnessEvaluator[T, N](context, environment)

    val selector: SelectionStrategy[Object] = new RouletteWheelSelection()

    val rng: Random = new MersenneTwisterRNG()

    val  engine: EvolutionEngine[WFSchedSolution] = new CoevolutionGenerationalEvolutionEngine(factory,
      pipeline,
      fitnessEvaluator,
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

    val result = engine.evolve(popSize, 1, new GenerationCount(iterationCount))
    WorkflowSchedulingProblem.solutionToSchedule(result, context, environment)
  }
}
