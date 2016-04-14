package itmo.escience.simenv

import ifmo.escience.dapris.common.base.algorithm.{BaseScheduleAlgorithm, BaseAlgorithm}
import ifmo.escience.dapris.common.entities.{Data, Workload, Environment, Task}
import itmo.escience.simenv.algorithms.Scheduler
import itmo.escience.simenv.algorithms.ga.GAScheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl._
import itmo.escience.simenv.simulator.BasicSimulator
import itmo.escience.simenv.utilities.Utilities._
import scala.collection.JavaConversions._


/**
  * Created by mikhail on 24.03.2016.
  */
object IPAdapter {

  def envAdapter(env: Environment): IPEnvironment = {
    val reliability = 1.0
    var nodes = List[DetailedNode]()

    for (n <- env.getNodes) {
      val adaptNode = new DetailedNode(n.getId,
        n.getName,
        n.getCpuTotal,
        n.getMemoryTotal,
        n.getGpuTotal,
        reliability,
        n.getParentId)
      nodes :+= adaptNode
    }

    val environment: IPEnvironment = new IPEnvironment(nodes)
    environment
  }

  def wlAdapter(wl: Workload): SingleAppWorkload = {
    val headTask = new HeadDaxTask(id = "Head", name = "PseudoHead", children = List[DaxTask]())
    var taskMap = new java.util.HashMap[String, DaxTask]()
    var headChildren = parseTasks(headTask, wl.getTasks, taskMap)
      headTask.children = headChildren
    val wf = new Workflow(id=headTask.id, name=headTask.name, headTask=headTask)
    new SingleAppWorkload(wf)
  }

  def parseTasks(parent: DaxTask, children: java.util.Set[Task], taskMap: java.util.HashMap[String, DaxTask]): List[DaxTask] = {
    var adaptChildren = List[DaxTask]()
    for (t <- children) {
      var task: DaxTask = null
      if (taskMap.contains(t.getId)) {
        task = taskMap.get(t.getId)
        task.parents :+= parent
      } else {
        task = new DaxTask(id = t.getId, name = t.getId, execTime = 1.0,
          inputData = adaptDataFiles(t.getInputData),
          outputData = adaptDataFiles(t.getOutputData),
          parents = List[DaxTask](parent),
          children = List[DaxTask]())
        val taskChildren = parseTasks(task, t.getChildTasks, taskMap)
        task.children = taskChildren
        taskMap.put(t.getId, task)
      }
      adaptChildren :+= task
    }
    adaptChildren
  }

  def adaptDataFiles(data: java.util.HashSet[Data]): List[DataFile] = {
    var result = List[DataFile]()
    for (d <- data) {
      val dataFile = new DataFile(id=d.getId, name=d.getName, volume=d.getSize)
      result :+= dataFile
    }
    result
  }


}
