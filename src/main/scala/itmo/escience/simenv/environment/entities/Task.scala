package itmo.escience.simenv.environment.entities

import itmo.escience.simenv.common.NameAndId

/**
 * Created by Mishanya on 14.10.2015.
 */

object TaskStatus {
  val FINISHED: String = "TaskStatusFinished"
  val RUNNING:String = "TaskStatusRunning"
  val FAILED:String = "TaskStatusFailed"
  val UNSTARTED:String = "TaskStatusUnstarted"
}

trait Task extends NameAndId[TaskId] {
  def inputData: List[DataFile]
  def outputData: List[DataFile]
  def parents: List[Task]
  def children: List[Task]
  def status: TaskStatus
}