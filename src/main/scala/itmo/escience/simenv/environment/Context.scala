package itmo.escience.simenv.environment

import itmo.escience.environment.entities.{ModellingTimesatmp, Schedule}
import itmo.escience.environment.modelling.{Environment, Estimator, Workload}
import itmo.escience.simenv.environment.modelling.{Estimator, Environment}


trait Context {

  def environment: Environment

  def estimator: Estimator

  def schedule: Schedule

  def workload: Workload

  def currentTime: ModellingTimesatmp

}






