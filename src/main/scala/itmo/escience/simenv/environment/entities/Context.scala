package itmo.escience.simenv.environment.entities

import itmo.escience.simenv.environment.modelling.{Environment, Estimator, Workload}


trait Context[T, N] {

  def environment: Environment[N]

  def estimator: Estimator[T,N]

  def schedule: Schedule

  def workload: Workload

  def currentTime: ModellingTimesatmp

}






