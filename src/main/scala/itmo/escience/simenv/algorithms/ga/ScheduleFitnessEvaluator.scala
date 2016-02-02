package itmo.escience.simenv.algorithms.ga

import java.util

import itmo.escience.simenv.algorithms.ga.env.{EnvConfSolution, EnvConfigurationProblem}

//import itmo.escience.simenv.algorithms.ga.env.EnvConfSolution
import itmo.escience.simenv.environment.entities.{Context, Node, Task}
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

  def getFitness(s: WFSchedSolution, e: EnvConfSolution): Double = {
//    val sAdapt = adaptation(s.copy, e.copy)
    val environment = EnvConfigurationProblem.solutionToEnvironment[T, N](e, ctx)
    val schedule = WorkflowSchedulingProblem.coevSolutionToSchedule[T, N](s, ctx, environment)
    schedule.makespan()
  }

//  def adaptation(sched: WFSchedSolution, env: EnvConfSolution): WFSchedSolution = {
//    var genes: List[MappedTask] = List[MappedTask]()
//    val emptyNodes = env.genSeq.filter(x => x.cap == 0).map(x => x.vmId)
//    val availableNodes = env.genSeq.filter(x => x.cap > 0).map(x => x.vmId)
//    for (x <- sched.genSeq) {
//      if (availableNodes.contains(x.nodeId)) {
//        genes :+= x
//      } else {
//        genes :+= new MappedTask(x.taskId, availableNodes(scala.util.Random.nextInt(availableNodes.size)))
//      }
//    }
//    new WFSchedSolution(genes)
//  }
}