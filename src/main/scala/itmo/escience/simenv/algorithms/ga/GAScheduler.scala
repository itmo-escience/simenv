package itmo.escience.simenv.algorithms.ga

import java.util
import java.util.Random

import itmo.escience.simenv.algorithms.{MinMinScheduler, HEFTScheduler, Scheduler}
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.maths.random.MersenneTwisterRNG
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection
import org.uncommons.watchmaker.framework._
import org.uncommons.watchmaker.framework.termination.GenerationCount

/**
  * Created by Mishanya on 22.01.2016.
  */

class GAScheduler(crossoverProb:Double, mutationProb: Double, swapMutationProb: Double,
                   popSize:Int, iterationCount: Int) extends Scheduler{


  override def schedule[T <: Task, N <: Node](context: Context[T, N], environment: Environment[N]): Schedule[T, N] = {
    val factory: ScheduleCandidateFactory[T, N] = new ScheduleCandidateFactory[T, N](context, environment)

    val operators: util.List[EvolutionaryOperator[WFSchedSolution]] = new util.LinkedList[EvolutionaryOperator[WFSchedSolution]]()
    operators.add(new ScheduleCrossoverOperator(crossoverProb))
    operators.add(new ScheduleMutationOperator[T, N](context, environment, mutationProb, swapMutationProb))

    val pipeline: EvolutionaryOperator[WFSchedSolution] = new EvolutionPipeline[WFSchedSolution](operators)

    val fitnessEvaluator: FitnessEvaluator[WFSchedSolution] = new ScheduleFitnessEvaluator[T, N](context, environment)

    val selector: SelectionStrategy[Object] = new RouletteWheelSelection()

    val rng: Random = new MersenneTwisterRNG()

    val  engine: EvolutionEngine[WFSchedSolution] = new ExtGenerationalEAlgorithm[T, N](factory,
      pipeline,
      fitnessEvaluator,
      selector,
      rng, popSize)

    if (false) {
      engine.addEvolutionObserver(new EvolutionObserver[WFSchedSolution]() {
        def populationUpdate(data: PopulationData[_ <: WFSchedSolution]) = {
          val best = data.getBestCandidate
          val bestMakespan = WorkflowSchedulingProblem.solutionToSchedule(best, context, environment).makespan()
          println(s"Generation ${data.getGenerationNumber}: $bestMakespan")
          println(s"Mean fitness: ${data.getMeanFitness}\n")
        }
      })
    }

    val heft_schedule = HEFTScheduler.schedule(context, environment)
    val min_schedule = MinMinScheduler.schedule(context, environment)
    val seeds: util.ArrayList[WFSchedSolution] = new util.ArrayList[WFSchedSolution]()
    val heft_sol = WorkflowSchedulingProblem.scheduleToSolution[T, N](heft_schedule, context, environment)
    seeds.add(heft_sol)
//    seeds.add(WorkflowSchedulingProblem.scheduleToSolution[T, N](min_schedule, context, environment))

    val result = engine.evolve(popSize, 1, seeds, new GenerationCount(iterationCount))
    WorkflowSchedulingProblem.solutionToSchedule(result, context, environment)
  }

}
