package itmo.escience.simenv.utilities

import itmo.escience.simenv.environment.entities.{DataFile, DaxTask, CapacityBandwidthResource}

import scala.collection.mutable
import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._

/**
  * Created by mikhail on 28.01.2016.
  */
object JSONParser {

def parseEnv(envPath: String, band: Double): List[CapacityBandwidthResource] = {
  var res = List[CapacityBandwidthResource]()
  val myString = Source.fromFile(envPath).mkString

  val myJSON = parse(myString)

  // Converting from JOjbect to plain object
  implicit val formats = DefaultFormats
  val size = myJSON.values.asInstanceOf[List[Any]].size
  for (i <- 0 until size) {
    val curJ = myJSON(i).values.asInstanceOf[Map[String, Any]]
    val id: String = curJ.get("id").get.asInstanceOf[String]
    val cpu: Double = curJ.get("totalCpuResources").get.asInstanceOf[Double]
    val node = new CapacityBandwidthResource(id=id, nominalCapacity=cpu, name=id, bandwidth=band)
    res :+= node
  }
  res
}

  def parseWorkload(workloadPath: String): List[DaxTask] = {
    var res = List[DaxTask]()
    val map = mutable.Map[DaxTask, List[String]]()
    val idMap = mutable.Map[String, DaxTask]()
    val myString = Source.fromFile(workloadPath).mkString
    val myJSON = parse(myString)

    // Converting from JOjbect to plain object
    implicit val formats = DefaultFormats
    val size = myJSON.values.asInstanceOf[List[Any]].size


    var ids: List[String] = List[String]()

    val symb = "6"

    for (i <- 0 until size) {
      val curJ = myJSON(i).values.asInstanceOf[Map[String, Any]]
      var id: String = curJ.get("id").get.asInstanceOf[String]
      if (ids.contains(id)) {
        id = id + symb
      }
      ids :+= id
      val cpu: Double = curJ.get("cpu.pcore.percent").get.asInstanceOf[Double]
      val data: Double = curJ.get("max.heap.size.mb").get.asInstanceOf[Double]
      var chs: List[String] = List[String]()
      val childIds: List[String] = curJ.get("children").get.asInstanceOf[List[String]]
      var children: List[String] = List[String]()
      for (c <- childIds) {
        if (children.contains(c)) {
          children :+= c + symb
        } else {
          children :+= c
        }
      }

      val task = new DaxTask(id=id, execTime=cpu, name=id, children=List[DaxTask](), parents=List[DaxTask](),
        inputData=List[DataFile](),
        outputData=List[DataFile](new DataFile(id + "_data", id + "_data", data)))
      res :+= task
      map.put(task, children)
      idMap.put(id, task)
    }
    for ((k, v) <- map) {
      for (c <- v) {
        val child: DaxTask = idMap.get(c).get
        k.children :+= child
        child.parents :+= k
        child.inputData ++= k.outputData
      }
    }
    for ((k, v) <- map) {
      if (k.parents.isEmpty) {
        k.inputData :+= new DataFile("empty", "empty", 0.0)
      }
    }

    res
  }

}

