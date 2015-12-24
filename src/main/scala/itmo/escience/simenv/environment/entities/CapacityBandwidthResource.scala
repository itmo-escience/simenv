package itmo.escience.simenv.environment.entities

import java.util


/**
  * Created by Mishanya on 23.12.2015.
  */
class CapacityBandwidthResource(val id: NodeId,
                                val name: String,
                                val nominalCapacity: Double,
                                val bandwidth: Double,
                                var reliability: Double = 1,
                                var parent: NodeId = NullNode.id,
                                var status: NodeStatus = Node.UP,
                                var taskList: util.HashMap[TaskId, DaxTask] = new util.HashMap[TaskId, DaxTask])
                                extends Node {

  var currentCapacity = nominalCapacity
  var currentBandwidth = bandwidth

  def addTask(t: DaxTask) = {
    taskList.put(t.id, t)
    if (!taskList.keySet().contains(t.parents.head.id)) {
      currentBandwidth -= t.inputVolume()
    }
    currentCapacity -= t.execTime
  }

  def canPlaceTask(t: DaxTask): Boolean = {
    if (taskList.keySet().contains(t.parents.head.id)) {
      t.execTime <= currentCapacity
    } else {
      t.execTime <= currentCapacity && t.inputVolume() <= currentBandwidth
    }
  }

  def removeNode(key: TaskId): DaxTask = {
    val task = taskList.remove(key)
    if (!taskList.keySet().contains(task.parents.head.id)) {
      currentBandwidth += task.inputVolume()
    }
    currentCapacity += task.execTime
    task
  }

  def getMap: util.HashMap[TaskId, DaxTask] = {
    taskList
  }

}
