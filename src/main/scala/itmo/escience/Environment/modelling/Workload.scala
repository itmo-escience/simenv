package itmo.escience.environment.modelling

import itmo.escience.environment.entities.Workflow

/**
 * Created by user on 27.11.2015.
 *  this entity defines methods to deal with current workload (individual apps which represents wfs)
 *  as currently executed as have been added to the queue
 */
trait Workload {
  def finishedApps: Seq[Workflow]
  def executingApps: Seq[Workflow]
  def enqueuedApps: Seq[Workflow]
}
