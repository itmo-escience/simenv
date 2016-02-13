package itmo.escience.simenv.utilities

import java.io.File
import java.util.UUID

import itmo.escience.simenv.environment.entities._

import scala.xml.{Node, XML}

/**
 * Created by user on 27.11.2015.
 */
object Utilities {

  def parseDAX(path:String, deadline: Double): Workflow[DaxTask] = {

    //TODO: correct assigning of id
    //TODO: generic should be here

    val file = new File(path)
    val dax = XML.loadFile(file)
    val jobs = dax \ "job"
    val childs = dax \ "child"


    val idf = (job:Node) => job.attribute("id").get.head.text
    val name = (job:Node) => job.attribute("name").get.head.text
    val runtime = (job:Node) => job.attribute("runtime").get.head.text.toDouble
    val refId = (job:Node) => job.attribute("ref").get.head.text

    def toDataFile(y:Node):DataFile = {
      val file = y.attribute("file").get.head.text
      val size = y.attribute("size").get.head.text.toDouble
      new DataFile(id=file, name=file, volume = size)
    }

    val tasks = jobs.map(x => {
      val inputData = (x \ "uses").
        filter(y => y.attribute("link").get.head.text == "input").
        map(y => toDataFile(y)).toList
      val outputData = (x \ "uses").
        filter(y => y.attribute("link").get.head.text == "output").
        map(y => toDataFile(y)).toList
      val task = new DaxTask(id=idf(x), name=name(x), execTime=runtime(x),
        inputData=inputData,
        outputData=outputData,
        parents=List(),
        children=List(), workflowId="wf")

      task.id -> task
    }).toMap

    for (child <- childs) {
      val childId = refId(child)
      val parents = (child \ "parent").map(x => tasks(refId(x)))
      tasks(childId).parents ++= parents
      val c = tasks(childId)
      for (p <- parents){
        p.children = c :: p.children
      }
    }

    val headtask = new HeadDaxTask(id= "000", name="headtask", List(), workflowId = "wf")
    val topLevelTasks = tasks.filter(x => x._2.parents.isEmpty).
      map(x => x._2).
      map(x => {
      x.parents = headtask :: x.parents
      x
    }).toList
    headtask.children = topLevelTasks

    new Workflow(id="wf", name="wf", deadline=deadline, headTask=headtask)

  }

  def generateId(): String = UUID.randomUUID().toString

}

object Units {
  implicit def toUnits(a:Int):Units = new Units(a)

}

class Units(a:Int) {
  //Volume
  def GB = a * 1024*1024*1024
  def MB = a * 1024*1024
  def KB = a * 1024
  //Network bandwidth
  def Mb_Sec = a * 1024*1024
  def Mbit_Sec = a * 1024*1024 / 8
  def Gbit_Sec = a * 1024*1024*1024 / 8
  def Kbit_Sec = a * 1024 /8
  //Calculation time
  def Sec = a
  def Min = a * 60
  def Hour = a * 60

  def % = a / 100
}
