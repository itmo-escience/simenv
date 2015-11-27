package itmo.escience.environment

import itmo.escience.environment.entities.{ModellingTimesatmp, Schedule}
import itmo.escience.environment.modelling.{Environment, Estimator, Workload}


trait Context {

  def environment: Environment

  def estimator: Estimator

  def schedule: Schedule

  def workload: Workload

  def currentTime: ModellingTimesatmp

}






