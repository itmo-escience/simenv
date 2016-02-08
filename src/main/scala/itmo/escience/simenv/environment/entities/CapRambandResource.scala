//package itmo.escience.simenv.environment.entities
//
//import java.util
//
//
///**
//  * Created by Mishanya on 23.12.2015.
//  */
//class CapRamBandResource(var id: NodeId,
//                         var name: String,
//                         val nominalCapacity: Double,
//                         val ram: Double,
//                         val bandwidth: Double,
//                         var reliability: Double = 1,
//                         var parent: NodeId = NullNode.id,
//                         var status: NodeStatus = Node.UP,
//                         var taskList: util.HashMap[TaskId, DaxTask] = new util.HashMap[TaskId, DaxTask])
//                                extends Node {
//
//  var currentCapacity = nominalCapacity
//  var currentRam = ram
//  var currentBandwidth = bandwidth
//
//  // Добавляет таску на нод, забирает ядра от нода
//  def addTask(t: DaxTask) = {
//    taskList.put(t.id, t)
//    currentCapacity -= t.execTime
//    currentRam -= t.ramReq
//  }
//
//  // Помещается ли данная таска на этом ноде?
//  def canPlaceTask(t: DaxTask): Boolean = {
//    t.execTime <= currentCapacity && t.ramReq <= currentRam
//  }
//
//  // Удаляет таску с нода, возвращает занятые ядра
//  def removeTask(key: TaskId): DaxTask = {
//    val task = taskList.remove(key)
//    currentCapacity += task.execTime
//    currentRam += task.ramReq
//    task
//  }
//
//  def getMap: util.HashMap[TaskId, DaxTask] = {
//    taskList
//  }
//
//}
