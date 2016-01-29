package itmo.escience.simenv.utilities

import itmo.escience.simenv.environment.entities.{DataFile, DaxTask, CapRamBandResource}

import scala.collection.mutable
import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._

/**
  * Created by mikhail on 28.01.2016.
  */
object JSONParser {

def parseEnv(envPath: String, band: Double): List[CapRamBandResource] = {
  var res = List[CapRamBandResource]()
  var myString : String = null
  if (envPath.contains(".json")) {


    myString = Source.fromFile(envPath).mkString
  }else {
    myString = envPath
  }

  val myJSON = parse(myString)

  // Converting from JOjbect to plain object
  implicit val formats = DefaultFormats
  val size = myJSON.values.asInstanceOf[List[Any]].size
  var j = 0
  for (i <- 0 until size) {
    val curJ = myJSON(i).values.asInstanceOf[Map[String, Any]]
    val id: String = curJ.get("id").get.asInstanceOf[String]
    val cpu: Double = curJ.get("totalCpuResources").get.asInstanceOf[Double]
    val ram: Double = curJ.get("totalMemoryResources").get.asInstanceOf[Double]
    val node = new CapRamBandResource(id=id, nominalCapacity=cpu, ram=ram, name="node" + j, bandwidth=band)
    res :+= node
    j += 1
  }
  res
}

  def parseWorkload(workloadPath: String): List[DaxTask] = {
    var res = List[DaxTask]()
    val map = mutable.Map[DaxTask, List[String]]()
    val idMap = mutable.Map[String, DaxTask]()
    var myString: String = null
    if (workloadPath.contains(".json")) {


      myString = Source.fromFile(workloadPath).mkString
    } else {
      myString = workloadPath
    }
    val myJSON = parse(myString)

    // Converting from JOjbect to plain object
    implicit val formats = DefaultFormats
    val size = myJSON.values.asInstanceOf[List[Any]].size


    var ids: List[String] = List[String]()

    val symb = "$"

    for (i <- 0 until size) {
      val curJ = myJSON(i).values.asInstanceOf[Map[String, Any]]
      var id: String = curJ.get("id").get.asInstanceOf[String]
      if (ids.contains(id)) {
        id = id + symb
        ids :+= id
      }
      ids :+= id
      val cpu: Double = curJ.get("cpu.pcore.percent").get.asInstanceOf[Double]
      var data: Double = 128
      val ram: Double = curJ.get("max.heap.size.mb").get.asInstanceOf[Double]
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
      if (children.isEmpty) {
        data = 0
      }

      val task = new DaxTask(id=id, execTime=cpu, ramReq=ram, name=id, children=List[DaxTask](), parents=List[DaxTask](),
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

