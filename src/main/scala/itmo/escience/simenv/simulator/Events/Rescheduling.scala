package itmo.escience.simenv.simulator.events

import itmo.escience.simenv.environment.entities.{Node, Task, ModellingTimestamp}
import itmo.escience.simenv.simulator.EventId

/**
  * Created by Mishanya on 31.01.2016.
  */
case class Rescheduling(id:EventId, name: String,
                   postTime: ModellingTimestamp,
                   eventTime: ModellingTimestamp) extends Event{
}
