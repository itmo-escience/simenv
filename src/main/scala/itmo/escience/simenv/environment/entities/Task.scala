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

  def volumeToTransfer(parent: DaxTask): Double = {
    if (!parents.contains(parent)) {
      //throw new InvalidArgumentException(s"the task ${parent.id} is not a parent for ${id}")
    }

    //TODO: need to properly implement intersect
    val files = this.inputData.intersect(parent.outputData)
    val transferVolume = files.foldLeft(0.0)((acc:Double, x:DataFile) => acc + x.cVolume)
    transferVolume
  }
}


case class HeadTask(id: TaskId, name:String, children:List[Task]) extends Task{

  override def execTime: Double = 0.0

  override def inputData: List[DataFile] = List()

  override def outputData: List[DataFile] = List()

  override def parents: List[DaxTask] = List()

  override def status: TaskStatus = TaskStatus.FINISHED
}