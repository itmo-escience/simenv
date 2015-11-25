package itmo.escience.Environment

import itmo.escience.Environment.Entities.{ScheduleItem, Node, Schedule}
import itmo.escience.Executors.Events.{EventHandler, EventQueue}

import scala.util.Random

/**
 * Created by Mishanya on 14.10.2015.
 */
class Context {
  var environment: Environment = _
  var workload: Workload = _
  var eventQueue: EventQueue = new EventQueue()
  var schedule: Schedule = new Schedule()
  var time: Double = 0
  var rnd: Random = new Random()
  var tag: Tag = new Tag()

  def setEnvironment(envrionment: Environment) = {
    this.environment = environment
  }

  def setWorkload(workload: Workload) = {
    this.workload = workload
  }

  // Add node to the context, and schedule
  def addNode(node: Node): Unit = {
    environment.resourceManager.nodes :+= node
    schedule.addNode(node)
  }

  // Add several nodes
  def addNodes(nodes: List[Node]): Unit = {
    nodes.foreach(n => addNode(n))
  }

  // Apply new schedule, start first tasks on nodes, and generate new events
  def applySchedule(newSchedule: Schedule): Unit = {
    var n: Node = null
    for (n <- newSchedule.map.keySet) {
      if (!schedule.map.contains(n)) {
        addNode(n)
      }
    }
    for (n <- schedule.map.keySet) {
      // Add new schedule items
      schedule.map = schedule.map.updated(n, schedule.map(n) ++ newSchedule.map(n))
      val nodeSchedule: List[ScheduleItem] = schedule.map(n)

      if (n.isFree() && nodeSchedule.nonEmpty) {
        // Try to start new task, if node is free
        var firstItem: ScheduleItem = nodeSchedule.head
        // Select, whether task will be finished or failed, and generate new events
        environment.estimator.taskFailer.taskFailer(firstItem, this)
      }
    }
  }
}
