package itmo.escience.simenv.algorithms

import itmo.escience.simenv.environment.entities._

/**
 * Created by user on 27.11.2015.
 */
object MinMinScheduler extends Scheduler{

  override def schedule[T <: Task, N <: Node](context: Context[T, N]): Schedule = {
    throw new NotImplementedError()
  }
}
