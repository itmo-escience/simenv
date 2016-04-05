package itmo.escience.simenv.simulator.events

import itmo.escience.simenv.common.NameAndId
import itmo.escience.simenv.environment.entities.ModellingTimestamp
import itmo.escience.simenv.simulator.EventId

/**
 * Created by Mishanya on 15.10.2015.
 */
trait Event extends NameAndId[EventId] {
  def postTime: ModellingTimestamp
  def eventTime: ModellingTimestamp
}
