package itmo.escience.simenv.environment.entities

import itmo.escience.simenv.environment.modelling.{Environment, Estimator, Workload}
import itmo.escience.simenv.simulator.events.EventQueue


trait Context[T <: Task, N <: Node] {

  def environment: Environment[N]

  def estimator: Estimator[T,N]

  def schedule: Schedule

  def workload: Workload[T]

  def currentTime: ModellingTimestamp

  def applySchedule(newSched: Schedule, queue: EventQueue)

  def setTime(newTime: ModellingTimestamp)

  def setEnvironment(newEnv: Environment[N])
}






