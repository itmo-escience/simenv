package itmo.escience.simenv.algorithms.storm

import java.util
import java.util.Random

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.utilities.Utilities.parseDAX


/**
  * Created by Mishanya on 23.12.2015.
  */
class StormSimulatedAnnealing(wfPath: String, n: Int, cores: Int, bandwidth: Int) {

  var nodes: util.HashMap[NodeId, CapacityBandwidthResource] = new util.HashMap[NodeId, CapacityBandwidthResource]
  var tasks: util.HashMap[TaskId, DaxTask] = new util.HashMap[TaskId, DaxTask]
  var schedule: util.HashMap[NodeId, List[TaskId]] = new util.HashMap[NodeId, List[TaskId]]

  val rnd: Random = new Random()

  def initialization(): Unit = {
    val sweeps = generateSweeps()
    schedule = initialSchedule(sweeps)
    println(schedule.toString)
    println("Initialization complete")
  }

  def runAlg() = {
    println("RUN!!!")
    var bestSchedule = schedule.clone().asInstanceOf[util.HashMap[NodeId, List[TaskId]]]
    var bestFitness: Double = evaluateFitness(bestSchedule)
    var curSchedule = bestSchedule.clone().asInstanceOf[util.HashMap[NodeId, List[TaskId]]]
    var curFitness: Double = bestFitness

    println(s"Best: $bestFitness; current: $curFitness")
    for (g <- 0 to 10) {
      val newSchedule = mutation(curSchedule)
      val newFitness: Double = evaluateFitness(newSchedule)
      if (newFitness < bestFitness) {
        bestSchedule = newSchedule
        bestFitness = newFitness
      }
      if (newFitness < curFitness || rnd.nextDouble() < energy(g, newFitness, curFitness)) {
        curFitness = newFitness
        curSchedule = newSchedule
      }
      println(s"Best: $bestFitness; current: $curFitness")
    }
    schedule = bestSchedule
    println(s"Result = $bestFitness")
  }

  def initialSchedule(sweeps: List[Workflow]): util.HashMap[NodeId, List[TaskId]] = {
    val initSchedule: util.HashMap[NodeId, List[TaskId]] = new util.HashMap[NodeId, List[TaskId]]
    var nodeIdx = 0
    for (s <- sweeps) {
      var node = new CapacityBandwidthResource(id="N" + nodeIdx, name="res_"+nodeIdx, nominalCapacity=cores,
        bandwidth=bandwidth)
      nodes.put(node.id, node)
      for (t <- s.tasks) {
        while (!node.canPlaceTask(t.asInstanceOf[DaxTask])) {
          nodeIdx += 1
          node = new CapacityBandwidthResource(id = "N" + nodeIdx, name = "res_" + nodeIdx, nominalCapacity = cores,
            bandwidth=bandwidth)
          if (!node.canPlaceTask(t.asInstanceOf[DaxTask])) {
            throw new IllegalArgumentException("Task can't be assigned on a free resource")
          }
          nodes.put(node.id, node)
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

  def energy(q: Int, next: Double, cur: Double): Double = {
    math.exp(-(next - cur) / q)
  }

  def evaluateFitness(solution: util.HashMap[NodeId, List[TaskId]]): Double = {
    val nodesNumber = solution.keySet().size()
    val overTransferNodes = evaluateOverTransferNodes(solution)
    println(s"nodes = $nodesNumber; overtransfer = $overTransferNodes")
    nodesNumber + overTransferNodes
  }

  def mutation(solution: util.HashMap[NodeId, List[TaskId]]): util.HashMap[NodeId, List[TaskId]] = {
    val mutant = solution.clone().asInstanceOf[util.HashMap[NodeId, List[TaskId]]]
    if (mutant.keySet().size < 2) {
      mutant
    }
    var placed: Boolean = false
    val keyset = solution.keySet().toArray
//    while (!placed) {
      val node1 = keyset(rnd.nextInt(keyset.size)).asInstanceOf[NodeId]
      val nodeTasks = mutant.get(node1)
      val task = nodeTasks(rnd.nextInt(nodeTasks.size))
      for (node2 <- keyset.filter(x => x != node1)) {
        if (nodes.get(node2).canPlaceTask(tasks.get(task)) && !placed) {
          nodes.get(node1).removeTask(task)
          nodes.get(node2).addTask(tasks.get(task))
          mutant.put(node1, mutant.get(node1).filter(x => x != task))
          mutant.put(node2.asInstanceOf[NodeId], schedule.get(node1) :+ task)
          if (nodes.get(node1).taskList.isEmpty) {
            mutant.remove(node1)
          }
          placed = true
        }
//      }

    }
    mutant
  }

  def evaluateOverTransferNodes(solution: util.HashMap[NodeId, List[TaskId]]): Double = {
    var overTransfer: Double = 0
    var overTransferNodes: Int = 0
    var iterator = solution.keySet().iterator()
    while (iterator.hasNext) {
      var n = iterator.next()
      var transfer = 0.0
      val nodeTasks = solution.get(n)
      for (t <- nodeTasks) {
        val task = tasks.get(t)
        if (task.parents.head.isInstanceOf[HeadDaxTask] ||
          !nodeTasks.contains(task.parents.head.id)) {
          transfer += task.inputVolume()
        }
        if (task.children.isEmpty ||
          !nodeTasks.contains(task.children.head.id)) {
          transfer += task.outputVolume()
        }
      }
      if (transfer > bandwidth) {
        overTransfer += (transfer - bandwidth)
      }
      overTransferNodes += 1
    }
    overTransfer
  }

}
