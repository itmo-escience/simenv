package itmo.escience.simenv.environment.entitiesimpl

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.modelling.{Environment, Estimator, Workload}

/**
 * Created by Nikolay on 11/29/2015.
 */
class BasicContext[T <: Task, N <: Node](var environment:Environment[N], var schedule: Schedule[T, N], var estimator: Estimator[T, N],
                         var currentTime:ModellingTimestamp,
                         var workload: Workload) extends Context[T, N] {

  def setTime(newTime: ModellingTimestamp): Unit = {
    if (newTime < currentTime) {
      throw new IllegalArgumentException("New time can't be less than current time")
    }
    currentTime = newTime
  }

  def setEnvironment(env: Environment[N]) = {
    environment = env
  }
}