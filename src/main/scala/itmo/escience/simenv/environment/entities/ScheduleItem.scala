package itmo.escience.simenv.environment.entities

import itmo.escience.simenv.common.NameAndId

trait ScheduleItem extends NameAndId[ScheduleItemId] {
  def startTime: ModellingTimestamp
  def endTime: ModellingTimestamp
  def status: ScheduleItemStatus
  def entity: NameAndId[String]
}

object TaskScheduleItemStatus {
  val RUNNING: ScheduleItemStatus = "running"
  val FINISHED: ScheduleItemStatus = "finished"
  val NOTSTARTED: ScheduleItemStatus = "notstarted"
  val FAILED: ScheduleItemStatus = "failed"
}


case class TaskScheduleItem(id: ScheduleItemId,
                            name: String,
                            startTime: ModellingTimestamp,
                            endTime: ModellingTimestamp,
                            status: ScheduleItemStatus,
                            node: CapacityBasedNode,
                            task: DaxTask) extends ScheduleItem {
  override def entity: NameAndId[String] = task

  def changeStatus(newStatus: String) : TaskScheduleItem = {
    if (newStatus != TaskScheduleItemStatus.RUNNING && newStatus != TaskScheduleItemStatus.FINISHED &&
      newStatus != TaskScheduleItemStatus.NOTSTARTED && newStatus != TaskScheduleItemStatus.FAILED) {
      throw new IllegalArgumentException("Status is not correct")
    }
    return new TaskScheduleItem(id=id, name=name, startTime=startTime, endTime=endTime, status=newStatus,
      node=node, task=task)
  }

  def setToFailed(failTime: ModellingTimestamp) : TaskScheduleItem = {
    if (failTime > endTime || failTime < startTime) {
      throw new IllegalArgumentException("Fail time is not correct")
    }
    return new TaskScheduleItem(id=id, name=name, startTime=startTime, endTime=failTime,
      status=TaskScheduleItemStatus.FAILED, node=node, task=task)
  }
}

//case class StageInScheduleItem(id:ScheduleItemId,
//                               name:String,
//                               startTime:ModellingTimesatmp,
//                               endTime:ModellingTimesatmp,
//                               status:ScheduleItemStatus,
//                               from: (DaxTask, NodeId),
//                              // this field is redundant
//                                to: (DaxTask, NodeId)) extends ScheduleItem
//
//case class ContainerUpScheduleItem(id:ScheduleItemId,
//                                   name:String,
//                                   startTime:ModellingTimesatmp,
//                                   endTime:ModellingTimesatmp,
//                                   status:ScheduleItemStatus,
//                                   container: Node) extends ScheduleItem
//
//case class ContainerDownScheduleItem(id:ScheduleItemId,
//                                   name:String,
//                                   startTime:ModellingTimesatmp,
//                                   endTime:ModellingTimesatmp,
//                                   status:ScheduleItemStatus,
//                                   container: NodeId) extends ScheduleItem