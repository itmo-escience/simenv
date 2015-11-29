package itmo.escience.simenv.algorithms

import itmo.escience.simenv.environment.Context
import itmo.escience.simenv.environment.entities.Schedule
import itmo.escience.simenv.environment.Context


/**
 * Created by user on 27.11.2015.
 */
trait Scheduler {

  def schedule(context: Context):Schedule

}
