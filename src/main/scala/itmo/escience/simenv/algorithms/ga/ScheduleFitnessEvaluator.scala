package itmo.escience.simenv.algorithms.ga

import java.util

import itmo.escience.simenv.algorithms.ga.env.EnvConfSolution
import itmo.escience.simenv.algorithms.vm.env.EnvConfigurationProblem
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.{BasicContext, BasicEnvironment, MultiWfWorkload}
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.FitnessEvaluator
import scala.collection.JavaConversions._

/**
  * Created by mikhail on 22.01.2016.
  */
class ScheduleFitnessEvaluator[T <: Task, N <: Node](ctx: Context[T, N], env: Environment[N]) extends FitnessEvaluator[WFSchedSolution] {
  override def isNatural: Boolean = false

  override def getFitness(t: WFSchedSolution, list: util.List[_ <: WFSchedSolution]): Double = {
    val schedule = WorkflowSchedulingProblem.solutionToSchedule[T, N](t, ctx, env)
    val makespan = schedule.makespan()
//    val deadline = ctx.workload.asInstanceOf[MultiWfWorkload[DaxTask]].deadlines.head._2
    var fitness = makespan
//    if (makespan > deadline) {
//      fitness += (makespan - deadline) * 666
//    }
    fitness

  }

  def getFitness(s: WFSchedSolution, e: EnvConfSolution): Double = {
    val environment = EnvConfigurationProblem.solutionToEnvironment[T, N](e, ctx)
    val schedule = WorkflowSchedulingProblem.solutionToSchedule[T, N](s, ctx, environment)
    val makespan = schedule.makespan()
    val cost = evaluateNodeCosts(schedule, environment)
    val deadline = ctx.workload.asInstanceOf[MultiWfWorkload[DaxTask]].deadlines.head._2
    var fitness = cost
    if (makespan > deadline) {
      fitness += (makespan - deadline) * 666
    }
    fitness
  }

  def evaluateNodeCosts(schedule: Schedule[T, N], environment: Environment[N]): Double = {
    val costs = ctx.asInstanceOf[BasicContext[T, N]].getCosts
    var result = 0.0
    for (n <- schedule.getMap.keySet()) {
      val itemList = schedule.getMap.get(n)
      val start = itemList.head.startTime
      val end = itemList.last.endTime
      val time = end - start
      val hours = (time / 3600).toInt + 1
      val cost = hours * costs.get(environment.nodeById(n).asInstanceOf[CapacityBasedNode].capacity)
      result += cost
    }
    result
  }
}