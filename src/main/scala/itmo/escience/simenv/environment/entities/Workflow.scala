package itmo.escience.simenv.environment.entities

import java.util

import itmo.escience.simenv.common.NameAndId

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import collection.JavaConversions._
/**
 * Created by user on 02.11.2015.
 */
class Workflow[T <: Task](val id: WorkflowId, val name:String, val headTask:Task, val deadline: ModellingTimestamp) extends NameAndId[WorkflowId] {
  private var _idToTasks:Map[TaskId, T] = null
  private var _allTasks:Seq[T] = null
  def tasks: Seq[T] = {

    if (_allTasks == null) {
      val allTasks: ArrayBuffer[T] = new ArrayBuffer[T]()
      val deque = new util.ArrayDeque[Task](headTask.children)
      while (!deque.isEmpty) {
        val task = deque.removeFirst()
        allTasks += task.asInstanceOf[T]
        for (t <- task.children) {
          deque.addLast(t)
        }
      }
      _allTasks = allTasks
    }
    _allTasks
  }

  def taskById(id:TaskId) = {
    if (_idToTasks == null){
      _idToTasks = tasks.map((task) => (task.id, task)).toMap
    }
    _idToTasks(id)
  }
}
