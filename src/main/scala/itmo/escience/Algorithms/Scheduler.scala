package itmo.escience.algorithms

import itmo.escience.environment.Context
import itmo.escience.environment.entities.Schedule


/**
 * Created by user on 27.11.2015.
 */
trait Scheduler {

  def schedule(context: Context):Schedule

}
