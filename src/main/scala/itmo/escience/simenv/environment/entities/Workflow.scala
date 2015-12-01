package itmo.escience.simenv.environment.entities

import java.util

import itmo.escience.simenv.common.NameAndId

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import collection.JavaConversions._
/**
 * Created by user on 02.11.2015.
 */
class Workflow(val id: WorkflowId, val name:String, val headTask:Task) extends NameAndId[WorkflowId] {
  private var _idToTasks:Map[TaskId, Task] = null
  private var _allTasks:Seq[Task] = null
  def tasks: Seq[Task] = {

    if (_allTasks == null) {
      val allTasks: ArrayBuffer[Task] = new ArrayBuffer[Task]()
      val deque = new util.ArrayDeque[Task](headTask.children)
      while (!deque.isEmpty) {
        val task = deque.removeFirst()
        allTasks += task
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
