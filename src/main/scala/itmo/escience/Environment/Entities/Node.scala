package itmo.escience.Environment.Entities

/**
 * Created by Mishanya on 14.10.2015.
 */
class Node (cName: String, cCapacity: Integer, cStatus: String = "working", cReliability: Double = 1) {
  val name: String = cName
  //TODO create enumeration for status
  var status: String = cStatus
  // Performance of this node
  var capacity: Integer = cCapacity
  // Current executing task on this node
  //TODO rename to be able to understand, that this is a ScheduleItem, not just a task
  var executedTask: ScheduleItem = _
  // Realibility of node
  var reliability: Double = cReliability
  //TODO add storages and data on them

  // Start new task execution
  //TODO rename parameter
  def runTask(item: ScheduleItem): Unit = {
    if (!isFree()) {
      throw new Exception("Node is busy. Can't start new task until the resource is released.")
    }
    executedTask = item
    //TODO Write statistic data about this new item into node's statistic
  }

  // Finish task execution, and return that ScheduleItem
  def releaseNode(): ScheduleItem = {
    //TODO Write data in node's statistics about this
    val finishedTask: ScheduleItem = executedTask
    executedTask = null
    return finishedTask
  }

  // Is node ready to execute tasks
  def isFree(): Boolean = executedTask != null

  def releaseTime(currentTime: Double): Double = {
    if (isFree()) {
      return currentTime
    } else {
      if (currentTime > executedTask.endTime) {
        throw new Exception("Current time more than current executed task's end time.")
      }
      return executedTask.endTime
    }
  }

}
