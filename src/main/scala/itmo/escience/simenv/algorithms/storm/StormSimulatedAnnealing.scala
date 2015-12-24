package itmo.escience.simenv.algorithms.storm

import java.util

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.utilities.Utilities.parseDAX

/**
  * Created by Mishanya on 23.12.2015.
  */
class StormSimulatedAnnealing(wfPath: String, n: Int, cores: Int, bandwidth: Int) {

  var nodes: util.HashMap[NodeId, CapacityBandwidthResource] = new util.HashMap[NodeId, CapacityBandwidthResource]
  var tasks: util.HashMap[TaskId, DaxTask] = new util.HashMap[TaskId, DaxTask]
  var schedule: util.HashMap[NodeId, List[TaskId]] = new util.HashMap[NodeId, List[TaskId]]

  def initialization(): Unit = {
    val sweeps = generateSweeps()
    schedule = initialSchedule(sweeps)
    println(schedule.toString)
    println("Initialization complete")
  }

  def runAlg() = {
    println("RUN!!!")
  }

  def initialSchedule(sweeps: List[Workflow]): util.HashMap[NodeId, List[TaskId]] = {
    var initSchedule: util.HashMap[NodeId, List[TaskId]] = new util.HashMap[NodeId, List[TaskId]]
    var nodeIdx = 0
    for (s <- sweeps) {
      var node = new CapacityBandwidthResource(id="N" + nodeIdx, name="res_"+nodeIdx, nominalCapacity=cores,
        bandwidth=bandwidth)
      nodes.put(node.id, node)
//      initSchedule.put(node.id, List[TaskId]())
      for (t <- s.tasks) {
        while (!node.canPlaceTask(t.asInstanceOf[DaxTask])) {
          nodeIdx += 1
          node = new CapacityBandwidthResource(id = "N" + nodeIdx, name = "res_" + nodeIdx, nominalCapacity = cores,
            bandwidth=bandwidth)
          if (!node.canPlaceTask(t.asInstanceOf[DaxTask])) {
            throw new IllegalArgumentException("Task can't be assigned on a full resource")
          }
          nodes.put(node.id, node)
//          initSchedule.put(node.id, List[TaskId]())
        }
        node.addTask(t.asInstanceOf[DaxTask])
        if (initSchedule.containsKey(node.id)) {
          initSchedule.put(node.id, initSchedule.get(node.id) :+ t.id)
        } else {
          initSchedule.put(node.id, List(t.id))
        }
      }
      nodeIdx += 1
    }
    initSchedule
  }

  def generateSweeps(): List[Workflow] = {
    var sweeps: List[Workflow] = List[Workflow]()
    for (i <- 0 to n) {
      val wf = parseDAX(wfPath, "_" + i)
      wf.tasks.foreach(t => tasks.put(t.id, t.asInstanceOf[DaxTask]))
      sweeps :+= wf
    }
    sweeps
  }

}
