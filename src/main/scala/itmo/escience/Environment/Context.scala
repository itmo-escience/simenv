package itmo.escience.Environment

import itmo.escience.Environment.Entities._
import itmo.escience.Executors.Events.{EventHandler, EventQueue}

import scala.collection.mutable
import scala.util.Random


/**
 * Represents main operations on resource sets
 * and give access to performance models
 */
trait Environment {

  type NodeId = String

  /**
   * Adds physical or virtual nodes to the pool of resources
   * the node is virtual if we cannot directly control physical machine when it runs
   * (for example, a node which has been bought of Amazon EC2 or GAE)
   * @param nodes sequence of nodes
   * @return
   */
  def addNodes(nodes: Seq[Node]):Unit

  def removeNodes(nodes: Seq[Node]):Unit

  def nodes(): Seq[Node]

  def addContainer(node:Node): Unit

  def removeContainer(node: Node): Unit

  def nodeOrContainerById(nodeId:NodeId):Node

  def changeNodeParams(newNodeDescription: Node)
  /**
   * Facade method for unifaction of access to performance models
   * @param task
   * @param nodeId
   * @return
   */
  def estimateCalculationTime(task:Task, nodeId:NodeId): Int

  def estimateDataTransferTime(pair1: (Task, NodeId), pair2: (Task, NodeId)):Int

  def estimateReliability(nodeId: NodeId)
}

trait Schedule {
  def mapping(): Map[NodeId, List[ScheduleItem]]
}







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

object Context {
  val Environment
  val Workload
  val Schedule
  val currentTime
  val eventQueue
  val Tag
}

object Workload {
  val Futures #not schedules
  val History #already executed
  val Present #current executing
  val WorkloadDynamicModel
}

object Schedule {
  val History
  val Plan # present and future
}

object eventQueue {
  val future
  val presentTask
  val History
}

object Simulator {
  val Context
  val handlersLogic
}

object Scheduler {

}

object Serializer {

}




