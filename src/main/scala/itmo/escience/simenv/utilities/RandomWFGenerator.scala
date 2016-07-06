package itmo.escience.simenv.utilities

import java.util

import itmo.escience.simenv.environment.entities._
import scala.collection.JavaConversions._

/**
  * Created by mikhail on 26.04.2016.
  */
object RandomWFGenerator {

  // Generate wf from map
  def generateWf(wfMap: util.HashMap[String, (Double, List[(String, Double)])], name: String): Workflow = {
    val ids = wfMap.keySet()

    val allChildren = wfMap.foldLeft(List[String]())((s, x) => s ++ x._2._2.map(y => y._1)).distinct
    val parents = ids.filter(x => !allChildren.contains(x))

    val allDataDependencies = wfMap.foldLeft(List[(String, String, Double)]())((s, x) =>
      s ++ x._2._2.map(y => (x._1, y._1, y._2)))

    val dataMap = new util.HashMap[String, DataFile]()
    for (item <- allDataDependencies) {
      val dId = item._1 + "_" + item._2
      val data = new DataFile(id=dId, name=dId, volume=item._3)
      dataMap.put(dId, data)
    }

    val taskMap = new util.HashMap[String, DaxTask]()

    for (id <- ids) {
      val item = wfMap.get(id)

      var input = List[DataFile]()
      // find all parent data dependencies
      val taskParents = allDataDependencies.filter(x => x._2 == id)
      if (taskParents.nonEmpty) {
        input = taskParents.foldLeft(List[DataFile]())((s, x) => s :+ dataMap.get(x._1 + "_" + x._2))
      }

      var output = List[DataFile]()
      // find all children data
      val taskChildren = allDataDependencies.filter(x => x._1 == id)
      if (taskChildren.nonEmpty) {
        output = taskChildren.foldLeft(List[DataFile]())((s, x) => s :+ dataMap.get(x._1 + "_" + x._2))
      }

      val task = new DaxTask(id = id, name = id, execTime = item._1,
        inputData = input,
        outputData = output,
        parents = List[DaxTask](),
        children = List[DaxTask]())

      taskMap.put(id, task)
    }

    for (edge <- allDataDependencies) {
      val from = taskMap.get(edge._1)
      val to = taskMap.get(edge._2)
      from.children :+= to
      to.parents :+= from
    }

    val headTasks = parents.map(x => taskMap.get(x))
    val headTask = new HeadDaxTask(id="head", name="head", children=headTasks.toList)
    for (h <- headTasks) {
      h.parents :+= headTask
    }
    val wf = new Workflow(id=name, name=name, headTask=headTask)
    wf
  }

}
