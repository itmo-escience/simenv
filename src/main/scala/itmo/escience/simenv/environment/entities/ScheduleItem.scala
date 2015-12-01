package itmo.escience.simenv.environment.entities

import itmo.escience.simenv.common.NameAndId

trait ScheduleItem extends NameAndId[ScheduleItemId] {
  def startTime: ModellingTimesatmp
  def endTime: ModellingTimesatmp
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
                            startTime: ModellingTimesatmp,
                            endTime: ModellingTimesatmp,
                            status: ScheduleItemStatus,
                            node: CapacityBasedNode,
                            task: DaxTask) extends ScheduleItem {
  override def entity: NameAndId[String] = task
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