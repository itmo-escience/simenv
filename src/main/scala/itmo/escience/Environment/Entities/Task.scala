package itmo.escience.Environment.Entities

/**
  * Created by Mishanya on 14.10.2015.
  */

trait BaseTask {
  def name: String
  def execTime: Double
  def inputData: List[DataFile]
  def outputData: List[DataFile]
  def parents: List[Task]
  def children: List[Task]
}

case class Task(name: String, execTime: Double,
                inputData: List[DataFile] = List(),
                outputData: List[DataFile] = List(),
                parents: List[Task],
                children: List[Task] ) extends BaseTask

case class HeadTask(name:String, children:List[Task]) extends BaseTask{
  override def execTime: Double = 0.0

  override def inputData: List[DataFile] = List()

  override def outputData: List[DataFile] = List()

  override def parents: List[Task] = List()
}

case class EndTask(name:String, parents:List[Task]) extends BaseTask
{
  override def execTime: Double = 0.0

  override def inputData: List[DataFile] = List()

  override def outputData: List[DataFile] = List()

  override def children: List[Task] = List()
}