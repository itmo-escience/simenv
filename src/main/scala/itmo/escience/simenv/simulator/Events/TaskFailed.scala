package itmo.escience.simenv.simulator.Events

import itmo.escience.environment.entities._
import itmo.escience.simulator._

/**
 * Created by Mishanya on 14.10.2015.
 */
case class TaskFailed(id:EventId, name: String,
                        postTime: ModellingTimesatmp,
                        eventTime: ModellingTimesatmp,
                        task: DaxTask,
                        node: Node) extends Event
