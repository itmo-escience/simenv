package itmo.escience.simenv.algorithms

import com.sun.javaws.exceptions.InvalidArgumentException
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.SingleAppWorkload

/**
 * Created by user on 27.11.2015.
 */
object MinMinScheduler extends Scheduler[DaxTask, CapacityBasedNode]{

  override def schedule(context: Context[DaxTask, CapacityBasedNode]): Schedule = {
    val currentSchedule = context.schedule

    // get unscheduled tasks.
    // scheduler can schedule non-fixed tasks
    // fixed tasks: finished, running, explicitly set as fixed
    // (possibly, the scheduler can break these limitations but it is an exceptional situation)

    //TODO; the alg should support several

    if (!context.workload.isInstanceOf[SingleAppWorkload]) {
      throw new UnsupportedOperationException(s"Invalid workload type ${context.workload.getClass}. " +
        s"Currently only SingleAppWorkload is supported")
    }

    val wf = context.workload.asInstanceOf[SingleAppWorkload].app

    //TODO: need to correctly implement scheduler for different use cases!

//    val newSchedule:Schedule = currentSchedule.fixedSchedule()
//    var tasksToSchedule = currentSchedule.restTasks(wf).asInstanceOf[List[DaxTask]]

    //Only for static case
    val newSchedule = Schedule.emptySchedule()
    var tasksToSchedule = wf.headTask.asInstanceOf[DaxTask].children
    val nodes = context.environment.nodes.filter(x => x.status == Node.UP)


    var scheduledTasks = tasksToSchedule.map(task => task.id).toSet
    val isReadyToRun = (x:Task) => x.parents.forall(p => scheduledTasks.contains(p.id))

    while (tasksToSchedule.nonEmpty) {

      // TODO: debug
      println(tasksToSchedule)

      val mintasks = tasksToSchedule.sortBy(x => x.execTime)

      // TODO: multiport model of traffic can be splitted in separate model
      val scheduleItems = mintasks.map( task => {
        val bestNode = nodes.minBy(node => {
          val transfer_overheads = task.parents.filter(x => !x.isInstanceOf[HeadDaxTask]).map( x =>context.estimator.
            calcTransferTime(from=(x, newSchedule.lastTaskItem(x.id).node), to=(task, node)))
          val transfer = if (transfer_overheads.isEmpty) 0.0 else transfer_overheads.max
          transfer + context.estimator.calcTime(task, node)
        })
        newSchedule.placeTask(task, bestNode, context)
      })

      // update list of scheduled tasks
      scheduledTasks ++= tasksToSchedule.map(task => task.id).toSet

      // children have to be verified on 'ready-to-run'
      tasksToSchedule = scheduleItems.sortBy(x => x.endTime).
        foldLeft(List[DaxTask]())((acc, x) => acc ++ x.task.children.filter(x => isReadyToRun(x))).toSet.toList

      //TODO: debug
      println(newSchedule.prettyPrint())
    }

    newSchedule
  }

}
