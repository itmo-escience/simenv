package itmo.escience.Environment.Events

import itmo.escience.Algorithms.Scheduler
import itmo.escience.Environment.Context
import itmo.escience.Environment.Entities.{Schedule, Task, Node, ScheduleItem}

/**
 * Created by Mishanya on 14.10.2015.
 */
object EventHandler {
  def handle(event: Event, ctx: Context, eq: EventQueue, scheduler: Scheduler): Unit = {
    // Event: TaskFailed
    if (event.isInstanceOf[TaskFailed]) {
      println("Task failed")
      // Rescheduling
      // Select free tasks, which can be replaced
      var schedTasks: List[Task] = scheduler.reschedule(ctx)
      // Create new schedule
      var newSchedule: Schedule = scheduler.schedule(ctx, schedTasks)
      // Apply new schedule
      ctx.applySchedule(newSchedule, eq)
    }

    // Event: TaskFinished
    if (event.isInstanceOf[TaskFinished]) {
      println("Task finished")
      val node: Node = event.node
      // End this task on node
      node.releaseNode()
      // If node has tasks in its schedule, try to start next task
      val nodeSched: List[ScheduleItem] = ctx.schedule.map(node)
      if (nodeSched.nonEmpty) {
        taskFailer(nodeSched.head, ctx, eq)
      }
    }

    // Event: TaskAdded

    // Event: NodeAdded

    // Event: NodeRemoved

    // Event: NodeChanged
  }

  // Roll the dice and select, whether task will be finished or failed
  //TODO test this function!!!
  def taskFailer(item: ScheduleItem, ctx: Context, eq: EventQueue): Unit = {
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
      eq.addEvent(new TaskFinished(item.task.name, item.task, item.endTime, node))
    } else {
      // Task will be failed
      eq.addEvent(new TaskFailed(item.task.name, item.task, item.endTime, node))
      // TODO correctly change status of this schedule item on "failed"
      item.isFailed = true
    }
  }
}
