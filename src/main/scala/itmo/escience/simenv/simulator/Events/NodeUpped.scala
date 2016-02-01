package itmo.escience.simenv.simulator.events

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.simulator._

/**
  * Created by Mishanya on 31.01.2016.
  */
case class NodeUpped  (id:EventId, name: String,
                  postTime: ModellingTimestamp,
                  eventTime: ModellingTimestamp,
                  node: Node) extends Event{

}
