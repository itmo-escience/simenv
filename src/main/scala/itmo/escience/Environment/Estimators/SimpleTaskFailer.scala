package itmo.escience.Environment.Estimators

import itmo.escience.Environment.{TaskFailer, Context}
import itmo.escience.Environment.Entities.{Node, ScheduleItem}
import itmo.escience.Executors.Events.{TaskFailed, TaskFinished, EventQueue}

/**
  * Created by Mishanya on 24.11.2015.
  */
class SimpleTaskFailer extends TaskFailer {

  // Roll the dice and select, whether task will be finished or failed
  //TODO test this function!!!
  def taskFailer(item: ScheduleItem, ctx: Context): Unit = {
    val node: Node = item.node
    if (ctx.rnd.nextDouble() < node.reliability) {
      // Task will be finished
      // Run first task on this node
      node.runTask(item)
      // Remove first schedule item from node's schedule
      val newNodeSched: List[ScheduleItem] = ctx.schedule.map(node).tail
      //TODO why "ctx.schedule.map(node) = ctx.schedule.map(node).tail" doesn't work?????
      ctx.schedule.map -= node
      ctx.schedule.map += (node -> newNodeSched)
      // Add task start event into event queue
      ctx.eventQueue.addEvent(new TaskFinished(item.task.name, item.task, item.endTime, node))
    } else {
      // Task will be failed
      ctx.eventQueue.addEvent(new TaskFailed(item.task.name, item.task, item.endTime, node))
      // TODO correctly change status of this schedule item on "failed"
      item.isFailed = true
    }
  }
}
