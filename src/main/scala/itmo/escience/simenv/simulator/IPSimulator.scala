package itmo.escience.simenv.simulator

import ifmo.escience.dapris.common.base.algorithm.BaseScheduleAlgorithm
import ifmo.escience.dapris.common.entities.Environment
import ifmo.escience.dapris.common.entities.Workload
import ifmo.escience.dapris.common.entities.{Workload, Environment}
import itmo.escience.simenv.IPAdapter._
import itmo.escience.simenv.algorithms.Scheduler
import itmo.escience.simenv.algorithms.ga.IPGAScheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl._
import itmo.escience.simenv.simulator.events._
import itmo.escience.simenv.utilities.{ScheduleVisualizer, SimLogger}

import scala.util.Random

/**
  * Created by mikhail on 15.04.2016.
  */
class IPSimulator(env: Environment, algorithm: BaseScheduleAlgorithm, workload: Workload) extends Simulator[DaxTask, DetailedNode]{

  val _env: IPEnvironment = envAdapter(env)
  val _workload: SingleAppWorkload = wlAdapter(workload)
  val _ctx = new BasicContext[DaxTask, DetailedNode](environment=_env, schedule=Schedule.emptySchedule[DaxTask, DetailedNode](),
    estimator=new IPEstimator(_env),
    currentTime=0.0,
    workload=_workload)

  val queue = new EventQueue()
  val rnd = new Random()
  SimLogger.setCtx(_ctx.asInstanceOf[Context[DaxTask, CapacityBasedNode]])
  val vis = new ScheduleVisualizer[DaxTask, DetailedNode]

  /**
    * generates and adds the very first event [[InitEvent]] to the event queue
    * Scheduling for initial state of environment have to be placed in the handler of this event
    */
  def init() = {
    queue.submitEvent(InitEvent.instance)
    SimLogger.log("Init event submitted")
  }

  /**
    * starts the simulation.
    * The simulation will finish when there is not any event in the queue
    */
  override def runSimulation(): Unit = {

    while (!queue.isEmpty) {
      val event = queue.next()
      SimLogger.logEvent(event)
      dispatchEvent(event)
      SimLogger.log("Event has been handled")
    }
    SimLogger.log("Finished")
    SimLogger.logSched(_ctx.schedule.asInstanceOf[Schedule[DaxTask, CapacityBasedNode]])

//    vis.drawSched(_ctx.schedule, _ctx.environment.asInstanceOf[CarrierNodeEnvironment[DetailedNode]])
  }

  /**
    * chooses an appropriate event handler for the current event
    *
    * @param event
    */
  def dispatchEvent(event: Event): Unit = event match {
    case InitEvent => onInitEvent()
    case ev: TaskStarted => onTaskStarted(ev)
    case ev: TaskFinished => onTaskFinished(ev)
    case ev: TaskFailed => onTaskFailed(ev)
    //    case ev: NodeFailed => onNodeFailed(ev)
    //    case ev: NodeUpped => onNodeUpped(ev)
    case ev: Rescheduling => onRescheduling(ev)
    case _ => throw new Exception(s"Unknown type of the event: ${event.getClass}")
  }

  def onInitEvent() = {
    SimLogger.log("Init event")

    // Scheduling
    algorithm.run()
    val schedule = algorithm.asInstanceOf[IPGAScheduler].getSchedule
//    val schedule = scheduler.schedule(ctx, ctx.environment)



    SimLogger.log("Init schedule is generated")
    // This function applies new schedule and generates events
    _ctx.applySchedule(schedule, queue)
    SimLogger.logSched(_ctx.schedule.asInstanceOf[Schedule[DaxTask, CapacityBasedNode]])

//    vis.drawSched(_ctx.schedule, _ctx.environment.asInstanceOf[CarrierNodeEnvironment[DetailedNode]])

    SimLogger.log("Init schedule has been applied")
    // Generate initial events
    init_helper()
    SimLogger.log("Initial task has been handled")
  }

  def init_helper() = {
    val schedMap = _ctx.schedule.getMap
    for (n <- _ctx.environment.nodes) {
      //TODO try to remove "asInstanceOf"
      if (!schedMap.containsKey(n.id)) {
        _ctx.schedule.addNode(n.id)
      }
      if (schedMap.get(n.id).nonEmpty) {
        val firstItem = schedMap.get(n.id).head.asInstanceOf[TaskScheduleItem[DaxTask, DetailedNode]]
        queue.submitEvent(new TaskStarted(id = firstItem.id, name = firstItem.name,
          postTime = _ctx.currentTime, eventTime = firstItem.startTime,
          task = firstItem.task, node = firstItem.node))
      }
    }
  }

  private def onTaskStarted(event: TaskStarted) = {
    _ctx.setTime(event.eventTime)
    val nid = event.node.id
    val schedMap = _ctx.schedule.getMap
    val nodeSched = schedMap.get(nid)
    val (iterator, curItem, counter) = _ctx.schedule.findItemInNodeSched(nid, event.id)
    schedMap.put(nid, nodeSched.take(counter))
    schedMap.get(nid).add(curItem.asInstanceOf[TaskScheduleItem[DaxTask, DetailedNode]].changeStatus(ScheduleItemStatus.RUNNING))
    taskFailer(curItem.asInstanceOf[TaskScheduleItem[DaxTask, DetailedNode]])
    while (iterator.hasNext) {
      schedMap.get(nid).add(iterator.next())
    }
  }

  private def onTaskFinished(event: TaskFinished) = {
    _ctx.setTime(event.eventTime)
    // 1) set status on finished
    val nid = event.node.id
    val schedMap = _ctx.schedule.getMap
    val nodeSched = schedMap.get(nid)
    val (iterator, curItem, counter) = _ctx.schedule.findItemInNodeSched(nid, event.id)

    schedMap.put(nid, nodeSched.take(counter))
    val finishedItem = curItem.asInstanceOf[TaskScheduleItem[DaxTask, DetailedNode]].changeStatus(ScheduleItemStatus.FINISHED)
    schedMap.get(nid).add(finishedItem)

    if (iterator.hasNext) {
      val nextItem = iterator.next().asInstanceOf[TaskScheduleItem[DaxTask, DetailedNode]]
      queue.submitEvent(new TaskStarted(id = nextItem.id, name = nextItem.name,
        postTime = _ctx.currentTime, eventTime = nextItem.startTime,
        task = nextItem.task, node = nextItem.node))
      schedMap.get(nid).add(nextItem)
    }
    while (iterator.hasNext) {
      schedMap.get(nid).add(iterator.next())
    }
  }

  def onTaskFailed(event: TaskFailed) = {
    task_failed_before(event)
    queue.submitEvent(new Rescheduling(id="reschedule", name="reschedule", postTime = _ctx.currentTime, eventTime = _ctx.currentTime))
  }

  def onRescheduling(event: Rescheduling) = {
    _ctx.setTime(event.eventTime)
    // Reschedule

    if (_ctx.schedule.restTasks().nonEmpty) {

      // Scheduling
      
      val sc = algorithm.asInstanceOf[IPGAScheduler].getSchedule
      // Scheduling

      queue.eq = queue.eq.filter(x => !x.isInstanceOf[TaskStarted])
      // Apply new schedule
      _ctx.applySchedule(sc, queue)

//      vis.drawSched(_ctx.schedule, _ctx.environment.asInstanceOf[CarrierNodeEnvironment[DetailedNode]])


      SimLogger.log("Rescheduling has been completed")
      SimLogger.logSched(sc.asInstanceOf[Schedule[DaxTask, CapacityBasedNode]])

      // submit new events, if it is required after the failed task
      task_failed_after()
    }
  }

  def task_failed_before(event: TaskFailed) = {
    _ctx.setTime(event.eventTime)

    // Mark this item as failed
    val nid = event.node.id
    val schedMap = _ctx.schedule.getMap
    val nodeSched = schedMap.get(nid)
    val (iterator, curItem, counter) = _ctx.schedule.findItemInNodeSched(nid, event.id)

    schedMap.put(nid, nodeSched.take(counter))
    val finishedItem = curItem.asInstanceOf[TaskScheduleItem[DaxTask, DetailedNode]].
      setToFailed(event.eventTime)

    schedMap.get(nid).add(finishedItem)

    while (iterator.hasNext) {
      schedMap.get(nid).add(iterator.next())
    }
  }

  def task_failed_after() = {
    val newFixedSchedule = _ctx.schedule.fixedSchedule()
    for (nid <- newFixedSchedule.nodeIds()) {
      val fixNodeSched = newFixedSchedule.getMap.get(nid)
      if (fixNodeSched.nonEmpty && fixNodeSched.last.status != ScheduleItemStatus.RUNNING) {
        val lastItem = fixNodeSched.last
        val (newIterator, newCurItem, newCounter) = _ctx.schedule.findItemInNodeSched(nid, lastItem.id)
        if (newIterator.hasNext) {
          val nextItem = newIterator.next().asInstanceOf[TaskScheduleItem[DaxTask, DetailedNode]]

          if (!queue.eq.map(x => x.id).toList.contains(nextItem.id)) {
            queue.submitEvent(new TaskStarted(id = nextItem.id, name = nextItem.name,
              postTime = _ctx.currentTime, eventTime = nextItem.startTime,
              task = nextItem.task, node = nextItem.node))
          }
        }
      } else {
        val newNodeSched = _ctx.schedule.getMap.get(nid)
        if (newNodeSched.nonEmpty) {
          val firstItem = newNodeSched.head.asInstanceOf[TaskScheduleItem[DaxTask, DetailedNode]]
          if (firstItem.status == ScheduleItemStatus.UNSTARTED && !queue.eq.map(x => x.id).toList.contains(firstItem.id)) {
            queue.submitEvent(new TaskStarted(id = firstItem.id, name = firstItem.name,
              postTime = _ctx.currentTime, eventTime = firstItem.startTime,
              task = firstItem.task, node = firstItem.node))
          }
        }
      }
    }
  }

  private def taskFailer(taskScheduleItem: TaskScheduleItem[DaxTask, DetailedNode]) = {
    //    taskScheduleItem.status = TaskScheduleItemStatus.RUNNING
    val dice = rnd.nextDouble()
    // TODO reliablity not via specific node
    if (dice < taskScheduleItem.node.asInstanceOf[DetailedNode].reliability) {
      // Task will be finished
      val taskFinishedEvent = new TaskFinished(id = taskScheduleItem.id, name = taskScheduleItem.name, postTime = _ctx.currentTime,
        eventTime = taskScheduleItem.endTime, taskScheduleItem.task, taskScheduleItem.node)
      queue.submitEvent(taskFinishedEvent)
    } else {
      // Task will be failed (random time between start and end of the current schedule item)
      val itemStart = taskScheduleItem.startTime
      val itemEnd = taskScheduleItem.endTime
      //      val failTime = rnd.nextDouble() * (itemEnd - itemStart) + itemStart
      val failTime = itemStart
      //      val taskFailedEvent = new NodeFailed(id = s"Node ${taskScheduleItem.node.id} failed", name = s"Node ${taskScheduleItem.node.id} failed", postTime = ctx.currentTime,
      //        eventTime = failTime, taskScheduleItem.node)
      //      queue.submitEvent(taskFailedEvent)
      val taskFailedEvent = new TaskFailed(id = taskScheduleItem.id, name = taskScheduleItem.name, postTime = _ctx.currentTime,
        eventTime = failTime, taskScheduleItem.task, taskScheduleItem.node)
      queue.submitEvent(taskFailedEvent)
    }
  }

  def getSchedule: Schedule[DaxTask, DetailedNode] = {
    _ctx.schedule
  }

  def getMakespan: Double = {
    _ctx.schedule.makespan()
  }



}
