package itmo.escience.simenv.environment.entities

import itmo.escience.simenv.common.NameAndId

/**
 * Created by Mishanya on 14.10.2015.
 */

trait Task extends NameAndId[TaskId] {
  def execTime: Double
  def inputData: List[DataFile]
  def outputData: List[DataFile]
  def parents: List[Task]
  def children: List[Task]
}


case class HeadTask(id: TaskId, name:String, children:List[Task]) extends Task{

  override def execTime: Double = 0.0

  override def inputData: List[DataFile] = List()

  override def outputData: List[DataFile] = List()

  override def parents: List[DaxTask] = List()
}

case class EndTask(id: TaskId, name:String, parents:List[Task]) extends Task {

  override def execTime: Double = 0.0

  override def inputData: List[DataFile] = List()

  override def outputData: List[DataFile] = List()

  override def children: List[DaxTask] = List()
}