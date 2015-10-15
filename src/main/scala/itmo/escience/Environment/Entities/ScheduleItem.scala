package itmo.escience.Environment.Entities

/**
 * Created by Mishanya on 14.10.2015.
 */
class ScheduleItem(cNode: Node, cTask: Task, cStartTime: Double, cEndTime: Double) {
  var node: Node = cNode
  var task: Task = cTask
  var startTime: Double = cStartTime
  var endTime: Double = cEndTime
  //TODO create enumeration for status
  var isFailed: Boolean = false
}
