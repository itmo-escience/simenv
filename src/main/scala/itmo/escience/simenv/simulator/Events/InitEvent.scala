package itmo.escience.simenv.simulator.Events

import itmo.escience.environment.entities.{ModellingTimesatmp, DaxTask, Node}
import itmo.escience.simulator.EventId

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
