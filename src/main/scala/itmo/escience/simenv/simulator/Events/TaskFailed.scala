package itmo.escience.simenv.simulator.events

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.simulator._

/**
 * Created by Mishanya on 14.10.2015.
 */
case class TaskFailed(id:EventId, name: String,
                      postTime: ModellingTimestamp,
                      eventTime: ModellingTimestamp,
                      task: Task,
                      node: Node) extends Event
