package itmo.escience.simenv.algorithms

import itmo.escience.simenv.environment.entities.{Task, Node, Context, Schedule}
import itmo.escience.simenv.environment.modelling.Environment


/**
 * Created by user on 27.11.2015.
 */
trait Scheduler {

  def schedule[T <: Task, N <: Node](context: Context[T,N], environment: Environment[N]): Schedule[T, N]

}
