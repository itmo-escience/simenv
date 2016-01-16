package itmo.escience.simenv.simulator

import itmo.escience.simenv.algorithms.Scheduler
import itmo.escience.simenv.algorithms.ga.cga.CoevGAScheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.{CarrierNodeEnvironment, BasicContext}
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.simulator.events.{InitEvent, TaskStarted, _}
import org.apache.logging.log4j.{LogManager, Logger}

import scala.util.Random


/**
 * Perform discrete event-drivent simulation of workflows execution
 * @param scheduler algorithm for scheduling, must implement Scheduler interface
 * @param ctx contains description of computational environments and may perform actions on it
 */
class SchedConfSimulator[T <: Task, N <: Node](scheduler: Scheduler[T, N], ctx:Context[T, N]) extends BasicSimulator[T, N](scheduler, ctx) {

  override val logger: Logger = LogManager.getLogger("logger")
  override val queue = new EventQueue()
  override val rnd = new Random()

  override def runSimulation(): Unit = {

    while (!queue.isEmpty) {
//      println(queue.print())
      val event = queue.next()

      dispatchEvent(event)
//      println("---next event---")
//      print(s"current time ${ctx.currentTime}")
//      println(s"event time ${event.eventTime}")
    }

    println("finish schedule")
    println(ctx.schedule.prettyPrint())
//    println(ctx.environment.asInstanceOf[CoreRamEnvironment].vms.map(x => s"${x.cores}; ${x.ram}"))
    println(s"makespan: ${ctx.schedule.makespan()}")
  }

  override def onInitEvent() = {

    // for any

    //val schedule = scheduler.schedule(ctx)
    //TODO: add logging here
    logger.trace("Init event")
    val (schedule, env) = scheduler.asInstanceOf[CoevGAScheduler].scheduleAndConfiguration(ctx.asInstanceOf[Context[DaxTask, Node]], ctx.environment.asInstanceOf[Environment[Node]])
    ctx.setEnvironment(env.asInstanceOf[Environment[N]])
    logger.trace("Init schedule is generated")
    // This function applies new schedule and generates events
    ctx.applySchedule(schedule, queue)

    println("Initial schedule:")
    println(ctx.schedule.prettyPrint())

    logger.trace("Init schedule has been applied")
    // Generate initial events
    val schedMap = ctx.schedule.getMap()
    for (n <- ctx.environment.nodes) {
      //TODO try to remove "asInstanceOf"
      if (!schedMap.containsKey(n.id)) {
        ctx.schedule.addNode(n.id)
      }
      if (schedMap.get(n.id).nonEmpty) {
        val firstItem = schedMap.get(n.id).head.asInstanceOf[TaskScheduleItem]
        queue.submitEvent(new TaskStarted(id = firstItem.id, name = firstItem.name,
          postTime = ctx.currentTime, eventTime = firstItem.startTime,
          task = firstItem.task, node = firstItem.node))
      }
    }
    logger.trace("Initial task has been handled")
  }

  override def onTaskFailed(event: TaskFailed) = {
    ctx.setTime(event.eventTime)
    //TODO: add logging here

    // Mark this item as failed
    val nid = event.node.id
    val schedMap = ctx.schedule.getMap()
    val nodeSched = schedMap.get(nid)
    val (iterator, curItem, counter) = ctx.schedule.findItemInNodeSched(nid, event.id)

    schedMap.put(nid, nodeSched.take(counter))
    val finishedItem = curItem.asInstanceOf[TaskScheduleItem].setToFailed(event.eventTime)
    schedMap.get(nid).add(finishedItem)

    while (iterator.hasNext) {
      schedMap.get(nid).add(iterator.next())
    }

    // Reschedule
    val (sc, env) = scheduler.asInstanceOf[CoevGAScheduler].scheduleAndConfiguration(ctx.asInstanceOf[Context[DaxTask, Node]], ctx.environment.asInstanceOf[Environment[Node]])
    ctx.setEnvironment(env.asInstanceOf[Environment[N]])

    println(s"Rescheduled schedule:\n ${sc.prettyPrint()}")
    queue.eq = queue.eq.filter(x => !x.isInstanceOf[TaskStarted])
    // Apply new schedule
    ctx.applySchedule(sc, queue)

    // submit new events, if it is required after the failed task

    val newFixedSchedule = ctx.schedule.fixedSchedule()
    for (nid <- newFixedSchedule.nodeIds()) {
      val fixNodeSched = newFixedSchedule.getMap().get(nid)
      if (fixNodeSched.nonEmpty && fixNodeSched.last.status != ScheduleItemStatus.RUNNING) {
        val lastItem = fixNodeSched.last
        val (newIterator, newCurItem, newCounter) = ctx.schedule.findItemInNodeSched(nid, lastItem.id)
        if (newIterator.hasNext) {
          val nextItem = newIterator.next().asInstanceOf[TaskScheduleItem]
          if (!queue.eq.map(x => x.id).toList.contains(nextItem.id)) {
            queue.submitEvent(new TaskStarted(id=nextItem.id, name=nextItem.name,
              postTime=ctx.currentTime, eventTime=nextItem.startTime,
              task=nextItem.task, node=nextItem.node))
          }
        }
      } else {
        val newNodeSched = ctx.schedule.getMap().get(nid)
        if (newNodeSched.nonEmpty) {
          val firstItem = newNodeSched.head.asInstanceOf[TaskScheduleItem]
          if (firstItem.status == ScheduleItemStatus.UNSTARTED && !queue.eq.map(x => x.id).toList.contains(firstItem.id)) {
            queue.submitEvent(new TaskStarted(id = firstItem.id, name = firstItem.name,
              postTime = ctx.currentTime, eventTime = firstItem.startTime,
              task = firstItem.task, node = firstItem.node))
          }
        }
      }
    }
  }
}


