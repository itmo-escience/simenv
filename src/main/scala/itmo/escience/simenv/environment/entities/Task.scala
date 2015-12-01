package itmo.escience.simenv.environment.entities

import com.sun.javaws.exceptions.InvalidArgumentException
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
  def execTime: Double
  def inputData: List[DataFile]
  def outputData: List[DataFile]
  def parents: List[Task]
  def children: List[Task]
  def status: TaskStatus
  def volumeToTransfer(parent: DaxTask): Double
}


//trait HeadTask extends Task{
//
//  override def execTime: Double = 0.0
//
//  override def inputData: List[DataFile] = List()
//
//  override def outputData: List[DataFile] = List()
//
//  override def parents: List[Task] = List()
//
//  override def status: TaskStatus = TaskStatus.FINISHED
//}