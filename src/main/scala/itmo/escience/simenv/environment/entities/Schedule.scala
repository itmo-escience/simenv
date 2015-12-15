package itmo.escience.simenv.environment.entities

import java.util

import itmo.escience.simenv.utilities.Utilities

import scala.collection.JavaConversions._


class InvalidScheduleException(msg:String) extends RuntimeException(msg)

/**
 * Created by Mishanya on 14.10.2015.
 */
class Schedule {

  def findTimeSlot(task: DaxTask, node: CapacityBasedNode, context: Context[DaxTask, CapacityBasedNode]): TaskScheduleItem = {
    // calculate time when all transfer from each node will be ended
    val stageInEndTime = task.parents.map({
      case _:HeadDaxTask => 0.0
      case x =>
        val parentItem = this.lastTaskItem(x.id).asInstanceOf[TaskScheduleItem]
        val transferTime = context.estimator.calcTransferTime(from = (parentItem.task, parentItem.node), to = (task, node))
        parentItem.endTime + transferTime
    }).max

    val runningTime = context.estimator.calcTime(task, node)

    val earliestStartTime = List(stageInEndTime, context.currentTime).max
    var foundStartTime = earliestStartTime

    // searching for a slot
    if (map.containsKey(node.id)) {
      val endOfLastTask =  if (map.get(node.id).size == 0) 0.0 else map.get(node.id).last.endTime
      if (map.get(node.id).nonEmpty && endOfLastTask > earliestStartTime ) {

        foundStartTime = endOfLastTask
        var st = endOfLastTask
        var end = endOfLastTask

        import scala.util.control.Breaks._
        breakable {
          for (x <- map.get(node.id).toList.reverseIterator) {

            if (end > earliestStartTime) {
              break
            }

            st = x.endTime
            if (end - st <= runningTime) {
              foundStartTime = st
            }
            end = x.startTime

          }
        }
      }
    }


    val newItem = new TaskScheduleItem(id=Utilities.generateId(),
      name = task.name,
      startTime=foundStartTime,
      endTime=foundStartTime + runningTime,
      status = TaskScheduleItemStatus.NOTSTARTED,
      node,
      task)
    newItem
  }

  // TODO: should be moved out of here or remade it universally

  def placeTask(task: DaxTask, node: CapacityBasedNode, context: Context[DaxTask, CapacityBasedNode]): TaskScheduleItem= {

    if (!map.containsKey(node.id)) {
      addNode(node.id)
    }

    val newItem = findTimeSlot(task, node, context)

    map.get(node.id).add(newItem)
    newItem
  }

  def placeTask(item: TaskScheduleItem) = {
    // 1. check if node exists
    //    if false create it
    if (!map.containsKey(item.node.id)) {
      addNode(item.node.id)
    }
    // it may be better to make a check for timeSlot
    // (in case of operation that can be performed separately,
    // for example: findTimeSlot and placeTask)
    // 2. check if the desirable time slot is free
    //    if true place the item

    // add new item
    map.get(item.node.id).add(item)
  }

  def scheduleItemsSeq(): Seq[ScheduleItem] = {
    map.foldLeft(List[ScheduleItem]())((acc, x) => acc ++ x._2).sortBy(x => x.startTime)
  }

  /**
   * checks if there is overlaps schedule items
   * (i.e. validaty of the schedule)
   * @param nodeId
   */
  def checkCrossing(nodeId: NodeId)= {
    var prev = 0.0
    for (x <- map.get(nodeId)){
      if (!(prev <= x.startTime &&  x.startTime < x.endTime)) {
        throw new InvalidScheduleException(s"the sequence of schedule items is broken (may be overlaps) for node ${nodeId}")
      }
      prev = x.endTime
    }

  }

  def makespan():Double = {
    val occupationTime = map.map({case (nodeId,items) => if (items.isEmpty) 0.0 else items.last.endTime})
    if(occupationTime.isEmpty) 0.0 else occupationTime.max
  }

  /**
   * This method have to return fixed part of schedule, which cannot be changed by scheduler
   * @return
   */
  def fixedSchedule(): Schedule = {
    var fixed = new Schedule()
    for (nid <- nodeIds()) {
      fixed.addNode(nid)
      val items = map.get(nid)
      for (item <- items) {
        if (item.status != TaskScheduleItemStatus.NOTSTARTED) {
          fixed.map.get(nid).add(item)
        }
      }
    }
    return fixed
  }

  /**
   * This method have to return list of tasks that need to be scheduled
   * @param wf
   * @return
   */
  def restTasks(wf: Workflow): List[Task] = {
    var rest = List[Task]()
    for (nid <- nodeIds()) {
      val items = map.get(nid)
      for (item <- items) {
        if (item.status == TaskScheduleItemStatus.NOTSTARTED) {
          rest = rest
        }
      }
    }
    return rest
  }

  // Schedule representation is map of nodes and list of schedule items
  private val map: java.util.HashMap[NodeId, scala.collection.mutable.SortedSet[ScheduleItem]] =
    new util.HashMap[NodeId,scala.collection.mutable.SortedSet[ScheduleItem]]()

  /**
   * items (sorted by startTime) related to the entity with {@entityId}
   * @param taskId
   * @return sorted sequence of scheduleitems
   */
  def taskItems(taskId:String):Seq[TaskScheduleItem] = {
    val itms = map.foldLeft(List[ScheduleItem]())((acc, x) => acc ++ x._2).filter({
      case t:TaskScheduleItem => t.task.id == taskId
      case _ => false
    }).map(x => x.asInstanceOf[TaskScheduleItem])
    itms
  }

  /**
   * Returns the last element of
   * @param taskId
   * @return
   */
  def lastTaskItem(taskId:String): TaskScheduleItem = {
    val itms = taskItems(taskId)
    if (itms.isEmpty) {
      throw new IllegalArgumentException(s"There is no items for the entity (id: ${taskId})")
    }
    itms.sortBy(x => x.startTime).last
  }

  /**
   * this method assumes installation of a new node.
   * There can be different situation: such as a new virtual node gets up,
   * or container splits the resources of the host
   * @param nodeId
   */
  def addNode(nodeId: NodeId): Unit = {
    map.put(nodeId, new scala.collection.mutable.TreeSet[ScheduleItem]()(new Ordering[ScheduleItem] {
      override def compare(x: ScheduleItem, y: ScheduleItem): Int = x.startTime.compare(y.startTime)
    }))
  }

  /**
   * Nodes are used for scheduling
   * @return
   */
  def nodeIds() = map.keySet().toSet

  def prettyPrint(): String = {
    //TODO: add correct interpolation
    val strs = map.toSeq.sortBy(x => x._1).foldLeft(List[String]())((acc, x) => {

        val nodeStr = s"Node - id: ${x._1}\n"

        // we need to use conversion toList here due to
      // map will not preserve the order of SortedSet (map don't know about custom ordering anything)
        val itemStr = x._2.toList.map({
          case y:TaskScheduleItem =>
            s"\tTask - id: ${y.entity.id} start: ${y.startTime} end: ${y.endTime} status: ${y.status}\n"
          case y =>
            s"\tItem (${y.getClass}) - id: ${y.entity.id} start: ${y.startTime} end: ${y.endTime} status: ${y.status}\n"
        })
        (acc :+ nodeStr) ++ itemStr
    })
    strs.mkString
  }

  def getMap() = map

//  def setMap(newMap: java.util.HashMap[NodeId, scala.collection.mutable.SortedSet[ScheduleItem]]) = {
//    map = newMap
//  }
}

object Schedule {
  def emptySchedule():Schedule = {
    new Schedule()
  }
}
