package itmo.escience.simenv.environment.entities

import itmo.escience.simenv.common.NameAndId

trait ScheduleItem extends NameAndId[ScheduleItemId] {
  def startTime: ModellingTimestamp
  def endTime: ModellingTimestamp
  def status: ScheduleItemStatus
  def entity: NameAndId[String]
}

object ScheduleItemStatus {
  val RUNNING: ScheduleItemStatus = "running"
  val FINISHED: ScheduleItemStatus = "finished"
  val UNSTARTED: ScheduleItemStatus = "unstarted"
  val FAILED: ScheduleItemStatus = "failed"
}


case class TaskScheduleItem[T <: Task, N <: Node](id: ScheduleItemId,
                                                  name: String,
                                                  startTime: ModellingTimestamp,
                                                  endTime: ModellingTimestamp,
                                                  status: ScheduleItemStatus,
                                                  node: N,
                                                  task: T) extends ScheduleItem {
  override def entity: NameAndId[String] = task

  def changeStatus(newStatus: String) : TaskScheduleItem[T, N] = {
    if (newStatus != ScheduleItemStatus.RUNNING && newStatus != ScheduleItemStatus.FINISHED &&
      newStatus != ScheduleItemStatus.UNSTARTED && newStatus != ScheduleItemStatus.FAILED) {
      throw new IllegalArgumentException("Status is not correct")
    }
    new TaskScheduleItem(id=id, name=name, startTime=startTime, endTime=endTime, status=newStatus,
      node=node, task=task)
  }

  def setToFailed(failTime: ModellingTimestamp) : TaskScheduleItem[T, N] = {
    if (failTime > endTime || failTime < startTime) {
      throw new IllegalArgumentException("Fail time is not correct")
    }
    new TaskScheduleItem(id=id, name=name, startTime=startTime, endTime=failTime,
      status=ScheduleItemStatus.FAILED, node=node, task=task)
  }
}