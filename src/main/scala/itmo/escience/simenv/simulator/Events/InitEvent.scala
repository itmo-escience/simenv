package itmo.escience.simenv.simulator.events

import itmo.escience.simenv.environment.entities.ModellingTimestamp
import itmo.escience.simenv.simulator.EventId

/**
 * Created by user on 02.11.2015.
 */
// TODO: BaseEvent should contain only two fields
case object InitEvent extends Event {

  def instance = this

  override def postTime: ModellingTimestamp = 0

  override def eventTime: ModellingTimestamp = 0

  override def id(): EventId = "0"

  override def name(): String = "InitEvent"
}
