package itmo.escience.simulator.events

import itmo.escience.environment.entities._
import itmo.escience.simulator._

/**
 * Created by Mishanya on 14.10.2015.
 */
case class TaskFailed(id:EventId, name: String,
                        postTime: ModellingTimesatmp,
                        eventTime: ModellingTimesatmp,
                        task: Task,
                        node: Node) extends Event
