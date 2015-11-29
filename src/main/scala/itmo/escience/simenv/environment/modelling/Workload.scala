package itmo.escience.simenv.environment.modelling

import itmo.escience.simenv.environment.entities.Workflow

/**
 * Created by user on 27.11.2015.
 *  this entity defines methods to deal with current workload (individual apps which represents wfs)
 *  as currently executed as have been added to the queue
 *  TODO: methods will be added here during development process
 */
trait Workload {
  def apps: Seq[Workflow]
}
