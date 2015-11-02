package itmo.escience.Environment

import itmo.escience.Environment.Entities.{Workflow, ScheduleItem, Node, Schedule}
import itmo.escience.Executors.Events.{EventHandler, EventQueue}

import scala.util.Random

/**
 * Created by Mishanya on 14.10.2015.
 */
class  Context(val workload:Array[Workflow]) {
  var schedule: Schedule = new Schedule()
  var time: Double = 0
  var nodes: List[Node] = List()
  var rnd: Random = new Random()

  // Add node to the context, and schedule
  def addNode(node: Node): Unit = {
    nodes :+= node
    schedule.addNode(node)
  }

  // Add several nodes
  def addNodes(nodes: List[Node]): Unit = {
    nodes.foreach(n => addNode(n))
  }

  // Apply new schedule, start first tasks on nodes, and generate new events
  def applySchedule(newSchedule: Schedule, eq: EventQueue): Unit = {
    var n: Node = null
    for (n <- schedule.map.keySet) {
      // Add new schedule items
      schedule.map = schedule.map.updated(n, schedule.map(n) ++ newSchedule.map(n))
      val nodeSchedule: List[ScheduleItem] = schedule.map(n)

      if (n.isFree() && nodeSchedule.nonEmpty) {
        // Try to start new task, if node is free
        var firstItem: ScheduleItem = nodeSchedule.head
        // Select, whether task will be finished or failed, and generate new events
        EventHandler.taskFailer(firstItem, this, eq)
      }
    }
  }
}
