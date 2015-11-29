package itmo.escience.simenv.environment.entitiesimpl

import itmo.escience.simenv.environment.entities.Workflow
import itmo.escience.simenv.environment.modelling.Workload

/**
 * Created by Nikolay on 11/29/2015.
 */
class SingleAppWorkload(val app: Workflow) extends Workload{
  override def apps: Seq[Workflow] = List(app)
}
