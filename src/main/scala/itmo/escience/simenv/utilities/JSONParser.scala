package itmo.escience.simenv.utilities

import java.util

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.utilities.Utilities._

import scala.collection.mutable
import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._

/**
  * Created by mikhail on 28.01.2016.
  */
object JSONParser {

def parseEnv(envPath: String, globNet: Int, localNet: Int): CarrierNodeEnvironment[CpuRamNode] = {
  var res = List[CpuRamNode]()
  var myString : String = null
  if (envPath.contains(".json")) {


    myString = Source.fromFile(envPath).mkString
  }else {
    myString = envPath
  }

  val myJSON = parse(myString)

  // Converting from JOjbect to plain object
  implicit val formats = DefaultFormats

  var carriers: List[CpuRamCarrier] = List[CpuRamCarrier]()
  var networks = List[Network]()

  val carrierSize = myJSON.values.asInstanceOf[List[Any]].size
  for (c <- 0 until carrierSize) {
    var nodes: List[CpuRamNode] = List[CpuRamNode]()

    val curNodes = myJSON(c).values.asInstanceOf[Map[String, Any]].get("nodes").get.asInstanceOf[List[Map[String, Any]]]

    val size = curNodes.size
    for (i <- 0 until size) {
      val curJ = curNodes(i)
      val id: String = curJ.get("id").get.asInstanceOf[String]
      val cpu: Double = curJ.get("totalCpuResources").get.asInstanceOf[Double]
      val ram: Double = curJ.get("totalMemoryResources").get.asInstanceOf[Double]
      val node = new CpuRamNode(id = id, cpu = cpu, ram = ram, name = s"res_${c}_node_$i", parent="res_" + c)
      nodes :+= node
    }
    val locNet = new Network(id=generateId(), name="local net", bandwidth=localNet, nodes)
    networks :+= locNet
    val curCarrier: CpuRamCarrier = new CpuRamCarrier(id="res_" + c,
                                                      name="res_"+c,
                                                      cpu=nodes.map(x => x.cpu).sum,
                                                      ram=nodes.map(x => x.ram).sum)
    for (n <- nodes) {
      curCarrier.addChild(n)
    }
    carriers :+= curCarrier
  }

  networks :+= new Network(id=generateId(), name="glob net", bandwidth=globNet, carriers)

  new CarrierNodeEnvironment[CpuRamNode](carriers, networks)
}


  def parseWorkload(workloadPath: String): java.util.HashMap[String, DaxTask] = {
    var res = new java.util.HashMap[String, DaxTask]()
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

    for (i <- 0 until size) {
      val curJ = myJSON(i).values.asInstanceOf[Map[String, Any]]
      val id: String = curJ.get("id").get.asInstanceOf[String]
      val name: String = curJ.get("name").get.asInstanceOf[String]
      val cpu: Double = curJ.get("cpu.pcore.percent").get.asInstanceOf[Double]
      val ram: Double = curJ.get("max.heap.size.mb").get.asInstanceOf[Double]

      var outData: Double = 0
      var children: List[String] = List[String]()
      var parents: List[String] = List[String]()
      var dataFromParents: List[Double] = List[Double]()
      var parentData: java.util.HashMap[String, Double] = new java.util.HashMap[String, Double]()
      var megabytesPerSecond: Double = 0
      if (curJ.get("megabytesOut").isDefined) {
        outData = curJ.get("megabytesOut").get.asInstanceOf[Double]
      }
      if (curJ.get("children").isDefined) {
        children = curJ.get("children").get.asInstanceOf[List[String]]
      }
      if (curJ.get("parents").isDefined) {
        parents = curJ.get("parents").get.asInstanceOf[List[String]]
      }
      if (curJ.get("megabytesFromParent").isDefined) {
        dataFromParents = curJ.get("megabytesFromParent").get.asInstanceOf[List[Double]]
        parentData = new java.util.HashMap[String, Double]()
        parents.zip(dataFromParents).foreach(x => parentData.put(x._1, x._2))
      }
      if (curJ.get("megabytesPerSecond").isDefined) {
        megabytesPerSecond = curJ.get("megabytesPerSecond").get.asInstanceOf[Double]
      }

      val task = new DaxTask(id=id, cpu=cpu, ram=ram, name=name,
        children=children, parents=parents, inputData = parentData, outputData = outData, maxData=megabytesPerSecond)
      res.put(task.id, task)
    }
    res
  }

}

