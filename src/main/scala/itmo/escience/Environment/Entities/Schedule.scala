package itmo.escience.Environment.Entities

import itmo.escience.Environment.Context

/**
 * Created by Mishanya on 14.10.2015.
 */
class Schedule {
  // Schedule representation is map of nodes and list of schedule items
  var map: Map[Node, List[ScheduleItem]] = Map()

  // Add new node to current schedule
  def addNode(node: Node): Unit = {
    map += (node -> List())
  }

  // Add new schedule item
  def addItem(node: Node, item: ScheduleItem): Unit = {
    map = map.updated(node, map(node) :+ item)
  }

  // Get time, when new schedule item can be assigned on certain node
  def getNodeFreeTime(node: Node, ctx: Context): Double = {
    val currentTime: Double = ctx.time
    // If node executing task, get end time of this task
    val nodeRelasedTime: Double = node.releaseTime(currentTime)
    // If schedule contains items on this node, get end time of last schedule item of this node
    var nodeLastSchedTime: Double = currentTime
    if (map(node).nonEmpty) {
      nodeLastSchedTime = map(node).last.endTime
    }
    return Math.max(nodeRelasedTime, nodeLastSchedTime)
  }
}
