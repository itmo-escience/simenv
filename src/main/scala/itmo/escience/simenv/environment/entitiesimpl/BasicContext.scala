package itmo.escience.simenv.environment.entitiesimpl

import itmo.escience.simenv.environment.entities.{Context, ModellingTimesatmp, Schedule}
import itmo.escience.simenv.environment.modelling.{Environment, Estimator, Workload}

/**
 * Created by Nikolay on 11/29/2015.
 */
class BasicContext[T, N](var environment:Environment, var schedule: Schedule, var estimator: Estimator[T, N],
                        var currentTime:ModellingTimesatmp,
                        var workload: Workload) extends Context[T, N]
