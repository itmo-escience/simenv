package itmo.escience.simenv.environment.entities

import java.util

import itmo.escience.simenv.common.NameAndId

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * Created by user on 02.11.2015.
 */
class Workflow(val id: WorkflowId, val name:String, val headTask:Task) extends NameAndId[WorkflowId] {
  def tasks: Seq[Task] = {
    import collection.JavaConversions._
    val allTasks:ArrayBuffer[Task] = new ArrayBuffer[Task]()
    val deque = new util.ArrayDeque[Task](headTask.children)
    while (!deque.isEmpty) {
      val task = deque.removeFirst()
      allTasks += task
      for(t <- task.children){
        deque.addLast(t)
      }
    }
    allTasks
  }
}
