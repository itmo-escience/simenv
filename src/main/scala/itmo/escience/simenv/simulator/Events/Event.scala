package itmo.escience.simenv.simulator.events

import itmo.escience.simenv.common.NameAndId
import itmo.escience.simenv.environment.entities.{ModellingTimesatmp, DaxTask, Node}
import itmo.escience.simenv.common.NameAndId
import itmo.escience.simenv.simulator.EventId

/**
 * Created by Mishanya on 15.10.2015.
 */
trait Event extends NameAndId[EventId] {
  def postTime: ModellingTimesatmp
  def eventTime: ModellingTimesatmp
}
