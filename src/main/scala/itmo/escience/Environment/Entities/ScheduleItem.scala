package itmo.escience.Environment.Entities

/**
 * Created by Mishanya on 14.10.2015.
 */
class ScheduleItem(cNode: Node, cTask: Task, cStartTime: Double, cEndTime: Double, cTransferTime: Double) {
  var node: Node = cNode
  var task: Task = cTask
  var startTime: Double = cStartTime
  // transfer time included into (startTime:endTime)
  var transferTime: Double = cTransferTime
  var endTime: Double = cEndTime
  //TODO create enumeration for status
  var isFailed: Boolean = false
}
