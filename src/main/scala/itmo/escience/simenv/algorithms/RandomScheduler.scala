package itmo.escience.simenv.algorithms

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.{SingleAppWorkload}
import itmo.escience.simenv.environment.modelling.Environment

import scala.util.Random

/**
 * Created by user on 02.12.2015.
 */
object RandomScheduler extends Scheduler[DaxTask, Node]{
  override def schedule(context: Context[DaxTask, Node], environment: Environment[Node]): Schedule = {

    if (!context.workload.isInstanceOf[SingleAppWorkload]) {
      throw new UnsupportedOperationException(s"Invalid workload type ${context.workload.getClass}. " +
        s"Currently only SingleAppWorkload is supported")
    }

    val wf = context.workload.asInstanceOf[SingleAppWorkload].app
    val newSchedule = Schedule.emptySchedule()
    var tasksToSchedule = wf.headTask.asInstanceOf[DaxTask].children
//    val nodes = context.environment.nodes.filter(x => x.status == Node.UP)
    val nodes = context.environment.nodes.filter(x => x.status == NodeStatus.UP)

    var scheduledTasks = tasksToSchedule.map(task => task.id).toSet
    val isReadyToRun = (x:Task) => x.parents.forall(p => scheduledTasks.contains(p.id))

    val random = new Random(System.currentTimeMillis())
    val randomNode = () => nodes(random.nextInt(nodes.length))

    while(tasksToSchedule.nonEmpty){

      val scheduleItems = tasksToSchedule.map( task => {
        val node = randomNode()
        newSchedule.placeTask(task, node, context)
      })

      // update list of scheduled tasks
      scheduledTasks ++= tasksToSchedule.map(task => task.id).toSet

      // children have to be verified on 'ready-to-run'
      tasksToSchedule = scheduleItems.sortBy(x => x.endTime).
        foldLeft(List[DaxTask]())((acc, x) => acc ++ x.task.children.filter(x => isReadyToRun(x))).distinct

    }

    newSchedule
  }
}
