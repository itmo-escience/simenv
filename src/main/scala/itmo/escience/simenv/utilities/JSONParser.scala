package itmo.escience.simenv.utilities

import itmo.escience.simenv.algorithms.ga.{MappedTask, WFSchedSolution}
import itmo.escience.simenv.algorithms.ga.env.{MappedEnv, EnvConfSolution}
import org.json4s.DefaultFormats
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.io.Source

/**
  * Created by mikhail on 28.01.2016.
  */
object JSONParser {

  def parseSolution(file: String): (WFSchedSolution, EnvConfSolution) = {
    var mappedTasks = List[(String, Int, Double)]()
    var sched = List[MappedTask]()
    var env = List[MappedEnv]()
    var myString: String = Source.fromFile(file).mkString

    val myJSON = parse(myString)

    // Converting from JOjbect to plain object
    implicit val formats = DefaultFormats

    val nodesSize = myJSON.values.asInstanceOf[List[Any]].size
    for (c <- 0 until nodesSize) {
      val curNode = myJSON(c).values.asInstanceOf[Map[String, Any]]
      val nodeId = curNode.get("resourse_id").get.asInstanceOf[String]
      val nodePower = curNode.get("power").get.asInstanceOf[String].toDouble

      env :+= new MappedEnv(nodePower)

      val tasks = curNode.get("tasks").get.asInstanceOf[List[Map[String, String]]]
      for (t <- tasks) {
        val taskId = t.get("name").get
        val startTime = t.get("start_time").get.toDouble
        mappedTasks :+= (taskId, c, startTime)
      }
    }
    mappedTasks = mappedTasks.sortBy(x => x._3)
    sched = mappedTasks.map(x => new MappedTask(x._1, x._2))

    (new WFSchedSolution(sched), new EnvConfSolution(env))
  }

}

