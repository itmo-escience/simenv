package itmo.escience.simenv.algorithms.wm

import java.util

import itmo.escience.simenv.environment.entities.{Context, Task, Node}
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.FitnessEvaluator

/**
  * Created by mikhail on 22.01.2016.
  */
class ScheduleFitnessEvaluator[T <: Task, N <: Node](ctx: Context[T, N], env: Environment[N]) extends FitnessEvaluator[WFSchedSolution] {
  override def isNatural: Boolean = false

  override def getFitness(t: WFSchedSolution, list: util.List[_ <: WFSchedSolution]): Double = {
    val schedule = WorkflowSchedulingProblem.solutionToSchedule[T, N](t, ctx, env)
    schedule.makespan()
  }
}

//trait FitnessCounter {
//
//  var counter:Int
//
//  abstract override def getFitness(t: WFSchedSolution, list: util.List[_ <: WFSchedSolution]): Double = {
//    super.getFitness(t, list)
//    coounter += 1
//  }
//}
