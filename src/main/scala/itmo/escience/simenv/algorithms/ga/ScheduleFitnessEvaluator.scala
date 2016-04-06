package itmo.escience.simenv.algorithms.ga

import java.util

import itmo.escience.simenv.algorithms.ga.env.{EnvConfigurationProblem, EnvConfSolution}
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.{BasicContext, BasicEnvironment, MultiWfWorkload}
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.utilities.MathFunctions
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

    val relPenalty = evaluateReliability(s, schedule, environment)
    var deadPenalty = 0.0

    if (makespan > deadline) {
      deadPenalty = makespan - deadline
    }

    val a = 1.0 // Cost
    val b = 0.7 // Makespan
    val c = 5.0 // Deadline penalty
    val d = 100.0 // Reliability penalty

    val fitness = a * cost + b * makespan + c * deadPenalty + d * relPenalty
    fitness
  }

  def evaluateCosts(schedule: Schedule[T, N], environment: Environment[N]): Double = {
    val costs = ctx.asInstanceOf[BasicContext[T, N]].getCosts
    var result = 0.0
    for (n <- schedule.getMap.keySet()) {
      val node = environment.nodeById(n).asInstanceOf[CapacityBasedNode]
      if (!node.fixed && schedule.getMap.get(n).nonEmpty) {
        val itemList = schedule.getMap.get(n)
        val start = itemList.head.startTime
        val end = itemList.last.endTime
        val time = (end - start) / 3600
        val cost = time * costs.get(environment.nodeById(n).asInstanceOf[CapacityBasedNode].capacity)
        result += cost
      }
    }
    result
  }

  def evaluateNodeCosts(schedule: Schedule[T, N], environment: Environment[N]): Double = {
    val costs = ctx.asInstanceOf[BasicContext[T, N]].getCosts
    var result = 0.0

    val allItems = schedule.scheduleItemsSeq().map(x => x.asInstanceOf[TaskScheduleItem[DaxTask, CapacityBasedNode]])
    for (item <- allItems) {
      if (!item.node.fixed) {
        val start = item.startTime
        val end = item.endTime
        val time = (end - start) / 3600
        val cost = time * costs.get(item.node.capacity)
        val taskItems = schedule.taskItems(item.task.id).filter(x => x != item)
        val lowerItems = taskItems.filter(x => x.endTime <= item.startTime)
        var chance = 0.0
        for (t <- lowerItems) {
          val exTime = t.endTime - t.startTime
          val tNode = t.node
          val tTask = t.task
          val rel = MathFunctions.getZPercents(tTask.asInstanceOf[DaxTask], exTime, tNode)
          val curRel = rel * tNode.asInstanceOf[CapacityBasedNode].reliability
          chance = chance + (1 - chance) * curRel
        }
        result += cost * (1 - chance)
      }
    }

    result
  }

  def evaluateReliability(sol: WFSchedSolution, schedule: Schedule[T, N], env: Environment[N]): Double = {
    val relMap = new util.HashMap[String, Double]()
    val fixNodes = env.fixedNodes.length
    for (g <- sol.genSeq) {
      if (!relMap.containsKey(g.taskId)) {
        relMap.put(g.taskId, 0.0)
      }
      val curRel = relMap.get(g.taskId)
      var nodeRel = ctx.asInstanceOf[BasicContext[DaxTask, CapacityBasedNode]].getFixRel
      if (g.nodeIdx >= fixNodes) {
        nodeRel = ctx.asInstanceOf[BasicContext[DaxTask, CapacityBasedNode]].getPubRel
      }
      relMap.put(g.taskId, curRel + (1 - curRel) * g.rel * nodeRel)
    }
    val res = relMap.values.count(x => x < 0.99)
    res
  }
}