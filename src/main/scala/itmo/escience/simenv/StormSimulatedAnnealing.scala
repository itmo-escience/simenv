package itmo.escience.simenv

import java.util
import java.util.Random

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.utilities.{JSONParser, StormScheduleVisualizer}
import itmo.escience.simenv.utilities.Utilities.parseDAX
import itmo.escience.simenv.utilities.JSONParser._
import scala.collection.JavaConversions._


/**
  * Created by Mishanya on 23.12.2015.
  */
//class StormSimulatedAnnealing(wfPath: String, n: Int, cores: Int, bandwidth: Int) {
class StormSimulatedAnnealing(workloadPath: String, envPath: String, bandwidth: Int) {

  // Мапа для содержания нодов по id
  var nodes: util.HashMap[NodeId, CapRamBandResource] = new util.HashMap[NodeId, CapRamBandResource]
  // Мапа для хранения тасок по id
  var tasks: util.HashMap[TaskId, DaxTask] = new util.HashMap[TaskId, DaxTask]
  // Расписание (На ноде располагаются таски)
  var schedule: util.HashMap[NodeId, List[TaskId]] = new util.HashMap[NodeId, List[TaskId]]

  val rnd: Random = new Random()

  // Объект визуализатора. Рисует начальное и конечное решение в
  // ./temp/lastRunSchedules
  var vis: StormScheduleVisualizer = null

  def initialization(): Unit = {

    // Ноды из JSON
    val nodesList =  JSONParser.parseEnv(envPath, bandwidth)
    val tasksList = JSONParser.parseWorkload(workloadPath)

    for (n <- nodesList) {
      nodes.put(n.id, n)
    }
    for (t <- tasksList) {
      tasks.put(t.id, t)
    }

    // Копируем наш начальный пайплайн или вф
//    val sweeps = generateSweeps()
    // Начальное решение (тупое)
    schedule = initialSchedule(tasksList, nodesList)

    vis = new StormScheduleVisualizer(tasks)

    println(schedule.toString)
    println("Initialization complete")
  }

  def runAlg() = {
    println("RUN!!!")
    // Количество итераций алгоритма
    val generations = 10000
    // Рисует первое расписание
    vis.drawSched(nodes)

    // Хранение лучшего решения.
    var bestSchedule = schedule.clone().asInstanceOf[util.HashMap[NodeId, List[TaskId]]]
    var bestFitness: Double = evaluateFitness(bestSchedule)
    var bestNodes: util.HashMap[NodeId, CapRamBandResource] = nodes.clone().asInstanceOf[util.HashMap[NodeId, CapRamBandResource]]

    // Текущее решение, с большей вероятностью только улучшается.
    var curSchedule = bestSchedule.clone().asInstanceOf[util.HashMap[NodeId, List[TaskId]]]
    var curFitness: Double = bestFitness
    var curNodes: util.HashMap[NodeId, CapRamBandResource] = nodes.clone().asInstanceOf[util.HashMap[NodeId, CapRamBandResource]]

    println(s"Init: $bestFitness")

    for (g <- 0 to generations) {
      // Производим мутацию над текущим решением. Получаем новое
      val (newSchedule, newNodes) = mutation(curSchedule, curNodes)
      // Считаем фитнесс для нового решения
      val newFitness: Double = evaluateFitness(newSchedule)
//      println(s"Mutant fitness = $newFitness")

      // Если новое лучше лучшего -> меняем лучшее на новое
      if (newFitness < bestFitness) {
        bestSchedule = newSchedule
        bestFitness = newFitness
        bestNodes = newNodes
      }

      // Считаем энергию (вероятность замены текущего решения на новое).
      // Энергия считается только когда новое решения хуже текущего
      var enrg = 1.0
      if (newFitness > curFitness) {
        // Сначала температуру
//        val temperature = 10 - (10 * (generations - g) / generations)
        val temperature = 5 -  4 * (generations - g + 1) / generations
        // Затем саму энергию
        enrg = energy(temperature, newFitness, curFitness)

      }
//      println(s"energy = $enrg")

      // Если вероятность проходит, тогда меняем текущее решение на новое (которое хуже)
      if (rnd.nextDouble() < enrg) {
        curFitness = newFitness
        curSchedule = newSchedule
        curNodes = newNodes
      }
//      println(s"Best: $bestFitness; current: $curFitness")
    }

    // Результат
    schedule = bestSchedule
    nodes = bestNodes
    println(s"Result = $bestFitness")
    println(schedule.toString)
    vis.drawSched(nodes)
  }

  // Создает начальное расписание на основе поочередного расположения тасок для одного пайплайна,
  // а затем копируя полученный результат для одного пайплайна для всех остальных
  def initialSchedule(tasksList: List[DaxTask], nodeList: List[CapRamBandResource]): util.HashMap[NodeId, List[TaskId]] = {
    val initSchedule: util.HashMap[NodeId, List[TaskId]] = new util.HashMap[NodeId, List[TaskId]]
    for (n <- nodeList) {
      initSchedule.put(n.id, List[String]())
    }
    for (t <- tasksList) {
      val availableNodes = nodeList.filter(x => x.canPlaceTask(t))
      if (availableNodes.isEmpty) {
        throw new IllegalArgumentException("PIZDEC")
      }
      val node = availableNodes(rnd.nextInt(availableNodes.size))
      initSchedule.put(node.id, initSchedule.get(node.id) :+ t.id)
      node.addTask(t)
    }
    initSchedule
  }
//  def initialScheduleOld(sweeps: List[Workflow]): util.HashMap[NodeId, List[TaskId]] = {
//    val initSchedule: util.HashMap[NodeId, List[TaskId]] = new util.HashMap[NodeId, List[TaskId]]
//    var nodeIdx = 0
//    for (s <- sweeps) {
//      // Для каждого свипа, берем поочередно его таски и размещаем на ноде.
//      // Когда текущий нод заполняется, создаем новый и продолжаем заполнять его.
//
//      // "CapacityBandwidthResource" - нод, который описан количеством ядер и максимальной
//      // пропускной способностью.
//
//      var node = new CapacityBandwidthResource(id="N" + nodeIdx, name="res_"+nodeIdx, nominalCapacity=666,
//        bandwidth=bandwidth)
//      nodes.put(node.id, node)
//      for (t <- s.tasks) {
//        while (!node.canPlaceTask(t.asInstanceOf[DaxTask])) {
//          nodeIdx += 1
//          node = new CapacityBandwidthResource(id = "N" + nodeIdx, name = "res_" + nodeIdx, nominalCapacity = 666,
//            bandwidth=bandwidth)
//          if (!node.canPlaceTask(t.asInstanceOf[DaxTask])) {
//            throw new IllegalArgumentException("Task can't be assigned on a free resource")
//          }
//          nodes.put(node.id, node)
//        }
//        node.addTask(t.asInstanceOf[DaxTask])
//        if (initSchedule.containsKey(node.id)) {
//          initSchedule.put(node.id, initSchedule.get(node.id) :+ t.id)
//        } else {
//          initSchedule.put(node.id, List(t.id))
//        }
//      }
//      nodeIdx += 1
//    }
//    initSchedule
//  }

  // Вычисление энергии для имитации отжига
  def energy(q: Int, next: Double, cur: Double): Double = {
    math.exp(-(next - cur) / q)
  }

  // Вычисление фитнесс функции для решений.
  // Количество использованных ресурсов + штрафы за каждый нод,
  // где был превышен канал передачи данных
  def evaluateFitness(solution: util.HashMap[NodeId, List[TaskId]]): Double = {
    // Считаем количество используемых нодов
    var nodesNumber = 0
    val nodeIter = solution.keySet().iterator()
    while (nodeIter.hasNext) {
      val node = nodeIter.next()
      if (solution.get(node).nonEmpty) {
        nodesNumber += 1
      }
    }

    // Вычисляем количество нодов, где был превышен канал передачи данных
    val overTransferNodes = evaluateOverTransferNodes(solution)
//    println(s"mutant fit: nodes = $nodesNumber; overtransfer = $overTransferNodes")

    // Результат
    nodesNumber + overTransferNodes
  }

  // Проведение мутации, возвращает новый объект, не меняет входной.
  def mutation(solution: util.HashMap[NodeId, List[TaskId]],
               curNodes: util.HashMap[NodeId, CapRamBandResource]):
  (util.HashMap[NodeId, List[TaskId]], util.HashMap[NodeId, CapRamBandResource]) = {

    // Копируем решение
    val mutant = solution.clone().asInstanceOf[util.HashMap[NodeId, List[TaskId]]]
    val newNodes = new util.HashMap[NodeId, CapRamBandResource]
    val cloneIter = curNodes.keySet().iterator()
    while (cloneIter.hasNext) {
      val key = cloneIter.next()
      val curNode = curNodes.get(key)
      val newNode = new CapRamBandResource(id=curNode.id,
        name=curNode.name, nominalCapacity=curNode.nominalCapacity, ram=curNode.ram,
        bandwidth=curNode.bandwidth)

      val tIter = curNode.taskList.keySet().iterator()
      while (tIter.hasNext) {
        val tId = tIter.next()
        newNode.addTask(tasks.get(tId))
      }

      newNodes.put(key, newNode)
    }

    // Удаляем ноды, на которых нет тасок. Удаление происходит только в случае,
    // когда ни на одном ноде нет превышения по каналу данных!
    if (mutant.keySet().size < 2) {
      (deleteEmptyNodes(mutant), newNodes)
    }

    // Идем с конца списка нодов.
    // Пытаемся взять таску и перенести на нод, который ближе по списку.
    var placed: Boolean = false
    val keyset = solution.keySet().toArray.reverse
    val nodeIterator = keyset.iterator
    while (nodeIterator.hasNext && !placed) {
      val nodeId = nodeIterator.next()
      val nodeTasks = mutant.get(nodeId)
      val taskIterator = nodeTasks.iterator
      while (taskIterator.hasNext && !placed) {
        // Выбрали таску, которую хотим перенести
        val taskId = taskIterator.next()
        val task = tasks.get(taskId)

        // Фильтруем узлы, на которых есть свободное количество ядер,
        // чтобы разместить эту таску
        val availableNodes = keyset.filter(x => x != nodeId && newNodes.get(x).currentCapacity >= task.execTime)
        if (availableNodes.length > 0) {
          // Выбираем любой из этих нодов, чтобы перетащить на него таску
          val transNodeId = availableNodes(rnd.nextInt(availableNodes.length))
          val transNode = newNodes.get(transNodeId)

          if (!transNode.canPlaceTask(task)) {
            throw new IllegalStateException("This node must be able to contain this task")
          }

          // Переносим таску.
          // Тут используется immutable коллекция Map, поэтому такой геморой.
          mutant.put(nodeId.asInstanceOf[NodeId], mutant.get(nodeId).filter(x => x != taskId))
          mutant.put(transNodeId.asInstanceOf[NodeId], mutant.get(transNodeId) :+ taskId)

          newNodes.get(nodeId).removeTask(taskId)
          newNodes.get(transNodeId).addTask(task)

          // Флаг, что перенос был осуществлен,
          // иначе будет поиск следующей таски для переноса.
          placed = true
        }
      }
    }

    (deleteEmptyNodes(mutant), newNodes)
  }

  // Удаляет неиспользуемые ноды из расписания. При условии, что нет превышений по каналам для
  // всех нодов.
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
  }

  // Вычисляет объем штрафной передачи данных,
  // т.е. превышающий возможности каналов на каждой ноде
  def evaluateOverTransferNodes(solution: util.HashMap[NodeId, List[TaskId]]): Double = {
    var overTransfer: Double = 0
//    var overTransferNodes: Int = 0
    val iterator = solution.keySet().iterator()
    // Идем по нодам
    while (iterator.hasNext) {
      val n = iterator.next()
      var transfer = 0.0
      val nodeTasks = solution.get(n)
      // Идем по таскам на этом ноде
      for (t <- nodeTasks) {
        val task = tasks.get(t)
        // Если таска-родитель НЕ на этом же ноде, прибавляем размер input.
        if (task.parents.isEmpty ||
          !nodeTasks.contains(task.parents.head.id)) {
          transfer += task.inputVolume()
        }
        // Если дочерняя таска НЕ на этом ноде, прибавляем output
        if (task.children.isEmpty ||
          !nodeTasks.contains(task.children.head.id)) {
          transfer += task.outputVolume()
        }
      }
      // Если полученное количество передачи данных превышает размер канала,
      // прибавляем штрафной объем данных к результату
      if (transfer > bandwidth) {
        overTransfer += (transfer - bandwidth)
      }
//      overTransferNodes += 1
    }
    // Результат
    overTransfer
  }

  def getCpuUtilization(solution:util.HashMap[NodeId, List[TaskId]]): Double = {
    val total = nodes.toList.filter(x => x._2.taskList.keySet().nonEmpty).foldLeft(0.0)((s, x) => s + x._2.nominalCapacity)
    val used = nodes.toList.filter(x => x._2.taskList.keySet().nonEmpty).foldLeft(0.0)((s, x) => s + x._2.currentCapacity)

    1 - used / total
  }

  def getTransfer(solution: util.HashMap[NodeId, List[TaskId]]): Double = {
    var overTransfer: Double = 0
    //    var overTransferNodes: Int = 0
    val iterator = solution.keySet().iterator()
    // Идем по нодам
    while (iterator.hasNext) {
      val n = iterator.next()
      var transfer = 0.0
      val nodeTasks = solution.get(n)
      // Идем по таскам на этом ноде
      for (t <- nodeTasks) {
        val task = tasks.get(t)
        // Если таска-родитель НЕ на этом же ноде, прибавляем размер input.
        if (task.parents.isEmpty ||
          !nodeTasks.contains(task.parents.head.id)) {
          transfer += task.inputVolume()
        }
        // Если дочерняя таска НЕ на этом ноде, прибавляем output
        if (task.children.isEmpty ||
          !nodeTasks.contains(task.children.head.id)) {
          transfer += task.outputVolume()
        }
      }
      // Если полученное количество передачи данных превышает размер канала,
      // прибавляем штрафной объем данных к результату
        overTransfer += transfer

      //      overTransferNodes += 1
    }
    // Результат
    overTransfer
  }

}


//количество нодов + количество проштрафленных * (1000)
