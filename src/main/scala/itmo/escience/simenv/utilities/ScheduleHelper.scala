package itmo.escience.simenv.utilities

import itmo.escience.simenv.environment.entities._
import sun.reflect.generics.reflectiveObjects.NotImplementedException

/**
 * Created by Nikolay on 11/29/2015.
 */
object ScheduleHelper {

  def checkStaticSchedule[T <: Task, N <: Node](ctx:Context[T, N]) = {
    for (app <- ctx.workload.apps) {
      checkStaticSchedule(app, ctx)
    }
  }
  def checkStaticSchedule[T <: Task, N <: Node](wf: Workflow, ctx: Context[T, N]) = {

    throw new NotImplementedError()

    val schedule = ctx.schedule

    // Check schedule for each app:
    // check dependency validaty:
    //  - a child is started only after the end of parent
    //  - a child has all parents in "notstarted" or "finished" states
    //  - if the goal to check finished schedule than all statuses should be in finished states

    for (task <- wf.tasks){
      val titems = schedule.items(task.id)

      onlyOnceFinishedOrNotstarted(titems)
      val titem = titems.last

      for(p <- task.parents){
        val pitems = schedule.items(p.id)

        //TODO: check the pitems is not empty

        val pitem = pitems.last
        //a child is started only after the end of parent
        if (pitem.endTime <= titem.startTime) {
          //TODO: make a special exception
          throw new NotImplementedError()
        }

        //a child has all parents in "notstarted" or "finished" states
        if (titem.status == TaskStatus.UNSTARTED && (pitem.status == TaskStatus.FINISHED || pitem.status == TaskStatus.UNSTARTED)){
          //TODO: make a special exception
          throw new NotImplementedError()
        }

        if (titem.status == TaskStatus.FINISHED && pitem.status == TaskStatus.FINISHED) {
          //TODO: make a special exception
          throw new NotImplementedError()
        }

        //if the goal to check finished schedule than all statuses should be in finished states
      }
    }
    throw new NotImplementedException()
  }

  private def onlyOnceFinishedOrNotstarted(items:Seq[ScheduleItem]) =  {
    throw new NotImplementedError()
    for (item <- items) {

    }
  }
}
