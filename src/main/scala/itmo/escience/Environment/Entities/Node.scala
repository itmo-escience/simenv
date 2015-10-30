package itmo.escience.Environment.Entities

/**
 * Created by Mishanya on 14.10.2015.
 */
class Node (cName: String, cCapacity: Integer, cStorage: Storage, cReliability: Double = 1, cStatus: String = "working") {
  val name: String = cName
  //TODO create enumeration for status
  var status: String = cStatus
  // Performance of this node
  var capacity: Integer = cCapacity
  // Current executing task on this node
  var executedItem: ScheduleItem = _
  // Realibility of node
  var reliability: Double = cReliability
  // Node's storage
  var storage: Storage = cStorage

  // Start new task execution
  def runTask(item: ScheduleItem): Unit = {
    if (!isFree()) {
      throw new Exception("Node is busy. Can't start new task until the resource is released.")
    }
    executedItem = item
    //TODO Write statistic data about this new item into node's statistic
  }

  // Finish task execution, and return that ScheduleItem
  def releaseNode(): ScheduleItem = {
    var file: DataFile = null
    for (file <- executedItem.task.outputData) {
      storage.writeFile(file)
    }
    //TODO Write data in node's statistics about this
    val finishedTask: ScheduleItem = executedItem
    executedItem = null
    return finishedTask
  }

  // Is node ready to execute tasks
  def isFree(): Boolean = executedItem == null

  def releaseTime(currentTime: Double): Double = {
    if (isFree()) {
      return currentTime
    } else {
      if (currentTime > executedItem.endTime) {
        throw new Exception("Current time more than current executed task's end time.")
      }
      return executedItem.endTime
    }
  }

}
