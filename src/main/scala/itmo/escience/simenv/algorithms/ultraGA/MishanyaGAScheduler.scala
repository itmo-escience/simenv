package itmo.escience.simenv.algorithms.ultraGA

import java.util
import java.util.Random

import itmo.escience.simenv.algorithms.{HEFTScheduler, Scheduler}
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.maths.random.MersenneTwisterRNG
import org.uncommons.watchmaker.framework._
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection
import org.uncommons.watchmaker.framework.termination.GenerationCount

/**
  * Created by Mishanya on 22.01.2016.
  */

class MishanyaGAScheduler(crossoverProb:Double, mutationProb: Double, swapMutationProb: Double,
                          popSize:Int, iterationCount: Int) extends Scheduler{


  override def schedule[T <: Task, N <: Node](context: Context[T, N], environment: Environment[N]): Schedule[T, N] = {
    val result = run[T, N](context, environment)
    MishanyaWorkflowSchedulingProblem.solutionToSchedule(result, context, environment)
  }

  def run[T <: Task, N <: Node](context: Context[T, N], environment: Environment[N]): MishanyaSolution = {
    val factory: MishanyaScheduleCandidateFactory[T, N] = new MishanyaScheduleCandidateFactory[T, N](context, environment)

    val operators: util.List[EvolutionaryOperator[MishanyaSolution]] = new util.LinkedList[EvolutionaryOperator[MishanyaSolution]]()
    val cross = new MishanyaScheduleCrossoverOperator(crossoverProb)
    val mutation = new MishanyaScheduleMutationOperator[T, N](context, environment, mutationProb, swapMutationProb)

    val fitnessEvaluator: MishanyaScheduleFitnessEvaluator[T, N] = new MishanyaScheduleFitnessEvaluator[T, N](context, environment)

    val selector: SelectionStrategy[Object] = new RouletteWheelSelection()

    val rng: Random = new MersenneTwisterRNG()

    val  engine: MishanyaGAScheme[T, N] = new MishanyaGAScheme[T, N](factory,
      mutation, cross,
      fitnessEvaluator,
      selector,
      rng, popSize)


    val heft_schedule = HEFTScheduler.schedule(context, environment)
    val seeds: util.ArrayList[MishanyaSolution] = new util.ArrayList[MishanyaSolution]()
    val heft_sol = MishanyaWorkflowSchedulingProblem.scheduleToSolution[T, N](heft_schedule, context, environment)
//    seeds.add(heft_sol)

    val result = engine.evolve(popSize, 1, seeds, new GenerationCount(iterationCount))
    result
  }

}
