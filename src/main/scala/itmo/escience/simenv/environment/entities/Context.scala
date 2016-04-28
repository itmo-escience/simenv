package itmo.escience.simenv.environment.entities

import itmo.escience.simenv.environment.modelling.{Environment, Estimator, Workload}


trait Context[T <: Task, N <: Node] {

  def environment: Environment[N]

  def estimator: Estimator[T,N]

  def schedule: Schedule[T, N]

  def workload: Workload

  def currentTime: ModellingTimestamp

  def setTime(newTime: ModellingTimestamp)

  def setEnvironment(newEnv: Environment[N])
}






