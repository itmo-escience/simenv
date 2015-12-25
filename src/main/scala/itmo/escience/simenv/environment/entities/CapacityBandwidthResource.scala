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
    currentCapacity -= t.execTime
  }

  def canPlaceTask(t: DaxTask): Boolean = {
    t.execTime <= currentCapacity
  }

  def removeTask(key: TaskId): DaxTask = {
    val task = taskList.remove(key)
    currentCapacity += task.execTime
    task
  }

  def getMap: util.HashMap[TaskId, DaxTask] = {
    taskList
  }

}
