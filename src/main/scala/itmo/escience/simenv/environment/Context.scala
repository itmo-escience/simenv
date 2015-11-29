package itmo.escience.simenv.environment

import itmo.escience.simenv.environment.entities.{ModellingTimesatmp, Schedule}
import itmo.escience.simenv.environment.modelling.{Environment, Estimator, Workload}


trait Context {

  def environment: Environment

  def estimator: Estimator[_,_]

  def schedule: Schedule

  def workload: Workload

  def currentTime: ModellingTimesatmp

}






