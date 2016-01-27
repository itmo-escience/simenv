package itmo.escience.simenv.algorithms

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.{MultiWfWorkload, SingleAppWorkload}
import itmo.escience.simenv.environment.modelling.Environment

import scala.util.Random

/**
 * Created by user on 02.12.2015.
 */
object RandomScheduler extends Scheduler{
  override def schedule[T <: Task, N <: Node](context: Context[T, N], environment: Environment[N]): Schedule[T, N] = {

    val newSchedule = Schedule.emptySchedule[T, N]()
    var tasksToSchedule = context.workload.asInstanceOf[MultiWfWorkload[T]].head.children.asInstanceOf[List[T]]
    val nodes = environment.nodes.filter(x => x.status == NodeStatus.UP)

    var scheduledTasks = tasksToSchedule.map(task => task.id).toSet
    val isReadyToRun = (x:T) => x.parents.forall(p => scheduledTasks.contains(p.id))

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
        foldLeft(List[T]())((acc, x) => acc ++ x.task.children.filter(x => isReadyToRun(x.asInstanceOf[T])).asInstanceOf[List[T]]).distinct

    }

    newSchedule
  }
}
