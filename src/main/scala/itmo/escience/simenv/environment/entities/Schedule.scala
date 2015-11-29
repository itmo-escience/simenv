package itmo.escience.simenv.environment.entities

import com.sun.xml.internal.ws.resources.ServerMessages

/**
 * Created by Mishanya on 14.10.2015.
 */
class Schedule {
  // Schedule representation is map of nodes and list of schedule items
  var map: Map[Node, List[ScheduleItem]] = Map()

  /**
   * items (sorted by startTime) related to the entity with {@entityId}
   * @param entityId
   * @return sorted sequence of scheduleitems
   */
  def items(entityId:String):Seq[ScheduleItem] = {
    throw new NotImplementedError()
  }

  /**
   * Returns the last element of
   * @param entityId
   * @return
   */
  def lastItem(entityId:String): ScheduleItem= {
    throw new NotImplementedError()
  }

//  // Add new node to current schedule
//  def addNode(node: Node): Unit = {
//    map += (node -> List())
//  }
//
//  // Add new schedule item
//  def addItem(node: Node, item: ScheduleItem): Unit = {
//    map = map.updated(node, map(node) :+ item)
//  }

//  // Get time, when new schedule item can be assigned on certain node
//  def getNodeFreeTime(node: Node, ctx: Context): Double = {
//    val currentTime: Double = ctx.time
//    // If node executing task, get end time of this task
//    val nodeRelasedTime: Double = node.releaseTime(currentTime)
//    // If schedule contains items on this node, get end time of last schedule item of this node
//    var nodeLastSchedTime: Double = currentTime
//    if (map(node).nonEmpty) {
//      nodeLastSchedTime = map(node).last.endTime
//    }
//    return Math.max(nodeRelasedTime, nodeLastSchedTime)
//  }
}

object Schedule {
  def emptySchedule():Schedule = {
    new Schedule()
  }
}
