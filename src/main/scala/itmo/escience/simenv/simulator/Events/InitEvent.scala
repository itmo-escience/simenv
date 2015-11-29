package itmo.escience.simenv.simulator.events

import itmo.escience.simenv.environment.entities.ModellingTimesatmp
import itmo.escience.simenv.simulator.EventId

/**
 * Created by user on 02.11.2015.
 */
// TODO: BaseEvent should contain only two fields
case object InitEvent extends Event {

  def instance = this

  override def postTime: ModellingTimesatmp = ???

  override def eventTime: ModellingTimesatmp = ???

  override def id(): EventId = ???

  override def name(): String = ???
}
