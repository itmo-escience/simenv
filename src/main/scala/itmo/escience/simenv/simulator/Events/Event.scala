package itmo.escience.simenv.simulator.Events

import itmo.escience.common.NameAndId
import itmo.escience.environment.entities.{ModellingTimesatmp, DaxTask, Node}
import itmo.escience.simenv.common.NameAndId
import itmo.escience.simulator.EventId

/**
 * Created by Mishanya on 15.10.2015.
 */
trait Event extends NameAndId[EventId] {
  def postTime: ModellingTimesatmp
  def eventTime: ModellingTimesatmp
}
