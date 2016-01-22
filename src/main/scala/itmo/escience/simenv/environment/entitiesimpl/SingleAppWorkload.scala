package itmo.escience.simenv.environment.entitiesimpl

import itmo.escience.simenv.environment.entities.{Task, DaxTask, Workflow}
import itmo.escience.simenv.environment.modelling.Workload

/**
 * Created by Nikolay on 11/29/2015.
 */
class SingleAppWorkload[T <: Task](val app: Workflow[T]) extends Workload[T]{
  override def apps: Seq[Workflow[T]] = List(app)
}
