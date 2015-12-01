package itmo.escience.simenv.environment.entities

import java.util

import com.sun.javaws.exceptions.InvalidArgumentException
import itmo.escience.simenv.utilities.Utilities

import scala.collection.JavaConversions._

/**
 * Created by Mishanya on 14.10.2015.
 */
class Schedule {

  // TODO: should be moved out of here or remade it universally
  def placeTask(task: DaxTask, node: CapacityBasedNode, context: Context[DaxTask, CapacityBasedNode]): TaskScheduleItem= {

    if (!map.containsKey(node.id)) {
      addNode(node.id)
    }

    // calculate time when all transfer from each node will be ended
    val stageInEndTime = task.parents.map({
      case _:HeadDaxTask => 0.0
      case x =>
        val parentItem = this.lastItem(x.id).asInstanceOf[TaskScheduleItem]
        parentItem.endTime + context.estimator.calcTransferTime(from = (parentItem.task, parentItem.node), to = (task, node))
    }).max

    val runningTime = context.estimator.calcTime(task, node)

    val earliestStartTime = List(stageInEndTime, context.currentTime).max
    var foundStartTime = earliestStartTime

    // searching for a slot
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

    val newItem = new TaskScheduleItem(id=Utilities.generateId(),
      name = task.name,
      startTime=foundStartTime,
      endTime=foundStartTime + runningTime,
      status = TaskScheduleItemStatus.NOTSTARTED,
      node,
      task)

    map.get(node.id).add(newItem)
    newItem
  }

  /**
   * This method have to return fixed part of schedule, which cannot be changed by scheduler
   * @return
   */
  def fixedSchedule(): Schedule = {
    throw new NotImplementedError()
  }

  /**
   * This method have to return list of tasks that need to be scheduled
   * @param wf
   * @return
   */
  def restTasks(wf: Workflow): List[Task] = {
    throw new NotImplementedError()
  }

  // Schedule representation is map of nodes and list of schedule items
  private val map: java.util.HashMap[NodeId, scala.collection.mutable.SortedSet[ScheduleItem]] =
    new util.HashMap[NodeId,scala.collection.mutable.SortedSet[ScheduleItem]]()

  /**
   * items (sorted by startTime) related to the entity with {@entityId}
   * @param entityId
   * @return sorted sequence of scheduleitems
   */
  def items(entityId:String):Seq[ScheduleItem] = {
    val itms = map.foldLeft(List[ScheduleItem]())((acc, x) => acc ++ x._2).filter(x => x.entity.id == entityId)
    itms
  }

  /**
   * Returns the last element of
   * @param entityId
   * @return
   */
  def lastItem(entityId:String): ScheduleItem= {
    val itms = items(entityId)
    if (itms.isEmpty) {
      throw new InvalidArgumentException(Array(s"There is no items for the entity (id: ${entityId})"))
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

  def prettyPrint(): String = {
    //TODO: add correct interpolation
    val strs = map.toSeq.sortBy(x => x._1).foldLeft(List[String]())((acc, x) => {

        val nodeStr = s"Node - id: ${x._1}\n"

        // we need to use conversion toList here due to
      // map will not preserve the order of SortedSet (map don't know about custom ordering anything)
        val itemStr = x._2.toList.map({
          case x:TaskScheduleItem =>
            s"\tTask - id: ${x.entity.id} start: ${x.startTime} end: ${x.endTime} status: ${x.status}\n"
          case x =>
            s"\tItem (${x.getClass}) - id: ${x.entity.id} start: ${x.startTime} end: ${x.endTime} status: ${x.status}\n"
        })
        (acc :+ nodeStr) ++ itemStr
    })
    strs.mkString
  }
}

object Schedule {
  def emptySchedule():Schedule = {
    new Schedule()
  }
}
