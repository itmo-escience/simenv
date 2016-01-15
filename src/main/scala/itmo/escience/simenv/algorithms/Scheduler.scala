package itmo.escience.simenv.algorithms

import itmo.escience.simenv.environment.entities.{Task, Node, Context, Schedule}
import itmo.escience.simenv.environment.modelling.Environment


/**
 * Created by user on 27.11.2015.
 */
trait Scheduler[T <: Task, N <: Node] {

  def schedule(context: Context[T,N], environment: Environment[N]): Schedule

}
