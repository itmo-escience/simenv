package itmo.escience.simenv.algorithms.storm

import java.util
import java.util.Random

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.utilities.StormScheduleVisualizer
import itmo.escience.simenv.utilities.Utilities.parseDAX


/**
  * Created by Mishanya on 23.12.2015.
  */
class StormSimulatedAnnealing(wfPath: String, n: Int, cores: Int, bandwidth: Int) {

  var nodes: util.HashMap[NodeId, CapacityBandwidthResource] = new util.HashMap[NodeId, CapacityBandwidthResource]
  var tasks: util.HashMap[TaskId, DaxTask] = new util.HashMap[TaskId, DaxTask]
  var schedule: util.HashMap[NodeId, List[TaskId]] = new util.HashMap[NodeId, List[TaskId]]

  val rnd: Random = new Random()

  var vis: StormScheduleVisualizer = null

  def initialization(): Unit = {
    val sweeps = generateSweeps()
    schedule = initialSchedule(sweeps)
    vis = new StormScheduleVisualizer(tasks)

    println(schedule.toString)
    println("Initialization complete")
  }

  def runAlg() = {
    println("RUN!!!")
    val generations = 10000
    vis.drawSched(nodes)
    var bestSchedule = schedule.clone().asInstanceOf[util.HashMap[NodeId, List[TaskId]]]
    var bestFitness: Double = evaluateFitness(bestSchedule)
    var bestNodes: util.HashMap[NodeId, CapacityBandwidthResource] = nodes.clone().asInstanceOf[util.HashMap[NodeId, CapacityBandwidthResource]]

    var curSchedule = bestSchedule.clone().asInstanceOf[util.HashMap[NodeId, List[TaskId]]]
    var curFitness: Double = bestFitness
    var curNodes: util.HashMap[NodeId, CapacityBandwidthResource] = nodes.clone().asInstanceOf[util.HashMap[NodeId, CapacityBandwidthResource]]

    println(s"Init: $bestFitness")

    for (g <- 0 to generations) {
      val (newSchedule, newNodes) = mutation(curSchedule, curNodes)
      val newFitness: Double = evaluateFitness(newSchedule)
//      println(s"Mutant fitness = $newFitness")
      if (newFitness < bestFitness) {
        bestSchedule = newSchedule
        bestFitness = newFitness
        bestNodes = newNodes
      }
      var enrg = 1.0
      if (newFitness > curFitness) {
//        val temperature = 10 - (10 * (generations - g) / generations)
        val temperature = 5 -  4 * (generations - g + 1) / generations
        enrg = energy(temperature, newFitness, curFitness)

      }
//      println(s"energy = $enrg")
      if (rnd.nextDouble() < enrg) {
        curFitness = newFitness
        curSchedule = newSchedule
        curNodes = newNodes
      }
//      println(s"Best: $bestFitness; current: $curFitness")
    }
    schedule = bestSchedule
    nodes = bestNodes
    println(s"Result = $bestFitness")
    println(schedule.toString)
    vis.drawSched(nodes)
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
    var nodesNumber = 0
    val nodeIter = solution.keySet().iterator()
    while (nodeIter.hasNext) {
      val node = nodeIter.next()
      if (solution.get(node).nonEmpty) {
        nodesNumber += 1
      }
    }

//    val nodesNumber = solution.keySet().size()

    val overTransferNodes = evaluateOverTransferNodes(solution)
//    println(s"mutant fit: nodes = $nodesNumber; overtransfer = $overTransferNodes")
    nodesNumber + overTransferNodes
  }


  def mutation(solution: util.HashMap[NodeId, List[TaskId]],
               curNodes: util.HashMap[NodeId, CapacityBandwidthResource]):
  (util.HashMap[NodeId, List[TaskId]], util.HashMap[NodeId, CapacityBandwidthResource]) = {

    val mutant = solution.clone().asInstanceOf[util.HashMap[NodeId, List[TaskId]]]
//    val newNodes = curNodes.clone().asInstanceOf[util.HashMap[NodeId, CapacityBandwidthResource]]
    val newNodes = new util.HashMap[NodeId, CapacityBandwidthResource]
    val cloneIter = curNodes.keySet().iterator()
    while (cloneIter.hasNext) {
      val key = cloneIter.next()
      val curNode = curNodes.get(key)
      val newNode = new CapacityBandwidthResource(id=curNode.id,
        name=curNode.name, nominalCapacity=curNode.nominalCapacity,
        bandwidth=curNode.bandwidth)

      val tIter = curNode.taskList.keySet().iterator()
      while (tIter.hasNext) {
        val tId = tIter.next()
        newNode.addTask(tasks.get(tId))
      }

      newNodes.put(key, newNode)
    }


    if (mutant.keySet().size < 2) {
      (deleteEmptyNodes(mutant), newNodes)
    }

    var placed: Boolean = false
    val keyset = solution.keySet().toArray.reverse
    val nodeIterator = keyset.iterator
    while (nodeIterator.hasNext && !placed) {
      val nodeId = nodeIterator.next()
      val nodeTasks = mutant.get(nodeId)
      val taskIterator = nodeTasks.iterator
      while (taskIterator.hasNext && !placed) {
        val taskId = taskIterator.next()
        val task = tasks.get(taskId)

        val availableNodes = keyset.filter(x => x != nodeId && newNodes.get(x).currentCapacity >= task.execTime)
        if (availableNodes.length > 0) {
          val transNodeId = availableNodes(rnd.nextInt(availableNodes.length))
          val transNode = newNodes.get(transNodeId)

          if (!transNode.canPlaceTask(task)) {
            throw new IllegalStateException("This node must be able to contain this task")
          }

          mutant.put(nodeId.asInstanceOf[NodeId], mutant.get(nodeId).filter(x => x != taskId))
          mutant.put(transNodeId.asInstanceOf[NodeId], mutant.get(transNodeId) :+ taskId)

          newNodes.get(nodeId).removeTask(taskId)
          newNodes.get(transNodeId).addTask(task)

          placed = true
        }
      }
    }
    (deleteEmptyNodes(mutant), newNodes)
  }

  def deleteEmptyNodes(solution: util.HashMap[NodeId, List[TaskId]]): util.HashMap[NodeId, List[TaskId]] = {
    if (evaluateOverTransferNodes(solution) > 0) {
      return solution
    }
    val repairedSolution: util.HashMap[NodeId, List[TaskId]] = new util.HashMap[NodeId, List[TaskId]]
    val iter = solution.keySet().iterator()
    while (iter.hasNext) {
      val n = iter.next()
      if (solution.get(n).nonEmpty) {
        repairedSolution.put(n, solution.get(n))
      }
    }
    repairedSolution
//    solution
  }

  def evaluateOverTransferNodes(solution: util.HashMap[NodeId, List[TaskId]]): Double = {
    var overTransfer: Double = 0
    var overTransferNodes: Int = 0
    val iterator = solution.keySet().iterator()
    while (iterator.hasNext) {
      val n = iterator.next()
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
