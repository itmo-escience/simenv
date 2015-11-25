package itmo.escience.Executors.Events

import com.sun.applet2.preloader.event.InitEvent
import itmo.escience.Algorithms.Scheduler
import itmo.escience.Environment.Context
import itmo.escience.Environment.Entities.{Schedule, Task, Node, ScheduleItem}

/**
 * Created by Mishanya on 14.10.2015.
 */
object EventHandler {
  def dispatchEvent(event: Event, ctx: Context, scheduler: Scheduler): Unit = {
    case InitEvent => onInitEvent(event, ctx, scheduler)
    case ev: TaskStarted => onTaskStarted(event, ctx, scheduler)
    case ev: TaskStarted => onTaskStarted(event, ctx, scheduler)
    case ev: TaskStarted => onTaskStarted(event, ctx, scheduler)
  }

  def onInitEvent(event: Event, ctx: Context, scheduler: Scheduler) = {

  }

  // Event: TaskFailed
  def onTaskFailed(event: Event, ctx: Context, scheduler: Scheduler) = {
    println("Task failed")
    // Rescheduling
    // Select free tasks, which can be replaced
    var schedTasks: List[Task] = scheduler.reschedule(ctx)
    // Create new schedule
    var newSchedule: Schedule = scheduler.schedule(ctx, schedTasks)
    // Apply new schedule
    ctx.applySchedule(newSchedule)
  }

  def onTaskFinished(event: Event, ctx: Context) = {
    println("Task finished")
    val node: Node = event.node
    // End this task on node
    node.releaseNode()
    // If node has tasks in its schedule, try to start next task
    val nodeSched: List[ScheduleItem] = ctx.schedule.map(node)
    if (nodeSched.nonEmpty) {
      // TODO refactor this sh..t!!!
      ctx.environment.estimator.taskFailer.taskFailer(nodeSched.head, ctx)
    }
  }

  def onTaskStarted(event: Event, ctx: Context, scheduler: Scheduler) = {
    throw new NotImplementedError()
  }

    // Event: TaskAdded

    // Event: NodeAdded

    // Event: NodeRemoved

    // Event: NodeChanged

}
