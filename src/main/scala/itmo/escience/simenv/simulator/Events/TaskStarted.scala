package itmo.escience.simenv.simulator.events

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.simulator._

/**
 * Created by user on 02.11.2015.
 */
case class TaskStarted(id:EventId, name: String,
                       postTime: ModellingTimestamp,
                       eventTime: ModellingTimestamp,
                       task: Task,
                       node: Node) extends Event
