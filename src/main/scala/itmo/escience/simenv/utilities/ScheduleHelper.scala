package itmo.escience.simenv.utilities

import itmo.escience.simenv.environment.entities._
import sun.reflect.generics.reflectiveObjects.NotImplementedException

/**
 * Created by Nikolay on 11/29/2015.
 */

class InvalidScheduleException(msg:String) extends RuntimeException(msg)

object ScheduleHelper {

  def checkStaticSchedule[T <: Task, N <: Node](ctx:Context[T, N], haveToBeFinished: Boolean=false):Unit = {
    for (app <- ctx.workload.apps) {
      checkStaticSchedule(app, ctx, haveToBeFinished)
    }
  }
  def checkStaticSchedule[T <: Task, N <: Node](wf: Workflow, ctx: Context[T, N], haveToBeFinished:Boolean):Unit = {

    val schedule = ctx.schedule

    println(schedule.prettyPrint())

    // Check schedule for each app:
    // check dependency validaty:
    //  - a child is started only after the end of parent
    //  - a child has all parents in "notstarted" or "finished" states
    //  - if the goal to check finished schedule than all statuses should be in finished states
    //  - crossings of different schedule items are not allowed

    for (task <- wf.tasks){

      val titems = schedule.items(task.id)
//      onlyOnceFinishedOrNotstarted(titems)
      val titem = titems.last

      if (haveToBeFinished && titem.status != TaskStatus.FINISHED){
        throw new InvalidScheduleException("All tasks in the schedule have to be finished (haveToBeFinished == True)")
      }

      for(p <- task.parents){

        // TODO: headtask checking. Remove head task at all later!!!
        if (p.parents.nonEmpty) {
          val pitems = schedule.items(p.id)

          if (pitems.isEmpty) {
            throw new InvalidScheduleException(s"Parent with id: ${p.id} of task ${task.id} is absent")
          }

          val pitem = pitems.last
          //a child is started only after the end of parent
          if (pitem.endTime > titem.startTime) {
            throw new InvalidScheduleException(s"Task (id: ${task.id}) is started (${titem.startTime}}) before the end (${pitem.endTime}}) of parent (id: ${p.id})")
          }

          //a child has all parents in "notstarted" or "finished" states
          if (titem.status == TaskStatus.UNSTARTED && (pitem.status == TaskStatus.FINISHED || pitem.status == TaskStatus.UNSTARTED)) {
            throw new InvalidScheduleException(s"Incorrect status of parent (id: ${p.id}, status: ${p.status}}) for child (id: ${task.id}, status: ${task.status}})")
          }

          if (titem.status == TaskStatus.FINISHED && pitem.status == TaskStatus.FINISHED) {
            throw new InvalidScheduleException(s"Incorrect status of parent (id: ${p.id}, status: ${p.status}}) for child (id: ${task.id}, status: ${task.status}})")
          }
        }
      }
    }
    //throw new NotImplementedException()
    // TODO: crossing
  }

//  private def onlyOnceFinishedOrNotstarted(items:Seq[ScheduleItem]) =  {
//    items.filter(x => x.status == TaskStatus.FINISHED)
//    for (item <- items) {
//
//    }
//  }
}
