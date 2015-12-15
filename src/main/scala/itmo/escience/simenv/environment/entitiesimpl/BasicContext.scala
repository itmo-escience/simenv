package itmo.escience.simenv.environment.entitiesimpl

import itmo.escience.simenv.environment.entities.{Context, ModellingTimestamp, Schedule}
import itmo.escience.simenv.environment.modelling.{Environment, Estimator, Workload}
import itmo.escience.simenv.simulator.events.EventQueue

/**
 * Created by Nikolay on 11/29/2015.
 */
class BasicContext[T, N](var environment:Environment[N], var schedule: Schedule, var estimator: Estimator[T, N],
                         var currentTime:ModellingTimestamp,
                         var workload: Workload) extends Context[T, N] {

  def applySchedule(newSched: Schedule, queue: EventQueue) = {
    schedule = newSched
  }

  def setTime(newTime: ModellingTimestamp): Unit = {
    if (newTime < currentTime) {
      throw new IllegalArgumentException("New time can't be less than current time")
    }
    currentTime = newTime
  }
}