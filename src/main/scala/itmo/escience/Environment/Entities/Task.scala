package itmo.escience.environment.entities

import itmo.escience.common.NameAndId

/**
 * Created by Mishanya on 14.10.2015.
 */

trait BaseTask extends NameAndId[TaskId] {
  def execTime: Double
  def inputData: List[DataFile]
  def outputData: List[DataFile]
  def parents: List[Task]
  def children: List[Task]
}

case class Task(id: TaskId, name: String, execTime: Double,
                inputData: List[DataFile] = List(),
                outputData: List[DataFile] = List(),
                parents: List[Task],
                children: List[Task] ) extends BaseTask

case class HeadTask(id: TaskId, name:String, children:List[Task]) extends BaseTask{

  override def execTime: Double = 0.0

  override def inputData: List[DataFile] = List()

  override def outputData: List[DataFile] = List()

  override def parents: List[Task] = List()
}

case class EndTask(id: TaskId, name:String, parents:List[Task]) extends BaseTask {

  override def execTime: Double = 0.0

  override def inputData: List[DataFile] = List()

  override def outputData: List[DataFile] = List()

  override def children: List[Task] = List()
}