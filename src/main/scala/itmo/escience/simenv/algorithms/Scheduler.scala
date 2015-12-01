package itmo.escience.simenv.algorithms

import itmo.escience.simenv.environment.entities.{Task, Node, Context, Schedule}


/**
 * Created by user on 27.11.2015.
 */
trait Scheduler[T, N] {

  def schedule(context: Context[T,N]):Schedule

}
