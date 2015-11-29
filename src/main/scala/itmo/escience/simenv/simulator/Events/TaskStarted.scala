package itmo.escience.simenv.simulator.Events

import itmo.escience.environment.entities._
import itmo.escience.simulator._

/**
 * Created by user on 02.11.2015.
 */
case class TaskStarted(id:EventId, name: String,
                        postTime: ModellingTimesatmp,
                        eventTime: ModellingTimesatmp,
                        task: DaxTask,
                        node: Node) extends Event
