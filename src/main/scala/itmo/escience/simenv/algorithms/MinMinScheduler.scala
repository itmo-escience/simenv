package itmo.escience.simenv.algorithms

import itmo.escience.simenv.environment.entities.{CapacityBasedNode, DaxTask, Context, Schedule}

/**
 * Created by user on 27.11.2015.
 */
object MinMinScheduler extends Scheduler{
  override def schedule[T <: DaxTask, N <: CapacityBasedNode](context: Context[T, N]): Schedule = {

    throw new NotImplementedError()
  }
}
