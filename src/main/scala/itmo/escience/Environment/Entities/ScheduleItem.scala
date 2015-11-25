package itmo.escience.Environment.Entities

trait ScheduleItem {
  val NOTSTARTED: ScheduleItemStatus = "NOTSTARTED"
  val SUCCEDED: ScheduleItemStatus = "SUCCEDED"
  val FAILED: ScheduleItemStatus = "FAILED"

  def id: ScheduleItemId
  def name: String
  def startTime: ModellingTimesatmp
  def endTime: ModellingTimesatmp
  def status: ScheduleItemStatus
}


case class TaskScheduleItem(id: ScheduleItemId,
                            name: String,
                            startTime: ModellingTimesatmp,
                            endTime: ModellingTimesatmp,
                            status: ScheduleItemStatus,
                            task: Task) extends ScheduleItem

case class StageInScheduleItem(id:ScheduleItemId,
                               name:String,
                               startTime:ModellingTimesatmp,
                               endTime:ModellingTimesatmp,
                               status:ScheduleItemStatus,
                               from: (Task, NodeId),
                              // this field is redundant
                                to: (Task, NodeId)) extends ScheduleItem

case class ContainerUpScheduleItem(id:ScheduleItemId,
                                   name:String,
                                   startTime:ModellingTimesatmp,
                                   endTime:ModellingTimesatmp,
                                   status:ScheduleItemStatus,
                                   container: Node) extends ScheduleItem

case class ContainerDownScheduleItem(id:ScheduleItemId,
                                   name:String,
                                   startTime:ModellingTimesatmp,
                                   endTime:ModellingTimesatmp,
                                   status:ScheduleItemStatus,
                                   container: NodeId) extends ScheduleItem