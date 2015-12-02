package itmo.escience.simenv.utilities

import itmo.escience.simenv.environment.entities._
import sun.reflect.generics.reflectiveObjects.NotImplementedException

/**
 * Created by Nikolay on 11/29/2015.
 */


object ScheduleHelper {

  def checkStaticSchedule[T <: Task, N <: Node](ctx:Context[T, N], haveToBeFinished: Boolean=false):Unit = {
    for (app <- ctx.workload.apps) {
      checkStaticSchedule(app, ctx.schedule, ctx, haveToBeFinished)
    }
  }
  def checkStaticSchedule[T <: Task, N <: Node](wf: Workflow, schedule: Schedule, ctx: Context[T, N], haveToBeFinished:Boolean):Unit = {

    // Check schedule for each app:
    // check dependency validaty:
    //  - a child is started only after the end of parent
    //  - a child has all parents in "notstarted" or "finished" states
    //  - if the goal to check finished schedule than all statuses should be in finished states
    //  - crossings of different schedule items are not allowed

    for (task <- wf.tasks){

      val titems = schedule.taskItems(task.id)
//      onlyOnceFinishedOrNotstarted(titems)
      val titem = titems.last

      if (haveToBeFinished && titem.status != TaskStatus.FINISHED){
        throw new InvalidScheduleException("All tasks in the schedule have to be finished (haveToBeFinished == True)")
      }

      for(p <- task.parents){

        if (p.parents.nonEmpty) {
          val pitems = schedule.taskItems(p.id)

          if (pitems.isEmpty) {
            throw new InvalidScheduleException(s"Parent with id: ${p.id} of task ${task.id} is absent")
          }

          val pitem = pitems.last
          //a child is started only after the end of parent
          if (pitem.endTime > titem.startTime) {
            throw new InvalidScheduleException(s"Task (id: ${task.id}) is started (${titem.startTime}}) before the end (${pitem.endTime}}) of parent (id: ${p.id})")
          }

          //a child has all parents in "notstarted" or "finished" states
          if (titem.status == TaskScheduleItemStatus.NOTSTARTED && (pitem.status != TaskScheduleItemStatus.FINISHED && pitem.status != TaskScheduleItemStatus.NOTSTARTED)) {
            throw new InvalidScheduleException(s"Incorrect status of parent (id: ${p.id}, status: ${pitem.status}}) " +
              s"for child (id: ${task.id}, status: ${titem.status}})")
          }

          if (titem.status == TaskScheduleItemStatus.FINISHED && pitem.status != TaskScheduleItemStatus.FINISHED) {
            throw new InvalidScheduleException(s"Incorrect status of parent (id: ${p.id}, status: ${pitem.status}}) " +
              s"for child (id: ${task.id}, status: ${titem.status}})")
          }
        }
      }
    }

    // overlaps check
    // for each node, take a sequence of ScheduleItem and verify there is not any crossing
    for(nodeId <-  schedule.nodeIds()){
      schedule.checkCrossing(nodeId)
    }
  }


//  private def onlyOnceFinishedOrNotstarted(items:Seq[ScheduleItem]) =  {
//    items.filter(x => x.status == TaskStatus.FINISHED)
//    for (item <- items) {
//
//    }
//  }
}
