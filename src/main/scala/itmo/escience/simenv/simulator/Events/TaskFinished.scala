package itmo.escience.simenv.simulator.events

import itmo.escience.simenv.environment.entities.{Task, ModellingTimesatmp, DaxTask, Node}
import itmo.escience.simenv.simulator.EventId

/**
 * Created by Mishanya on 15.10.2015.
 */
case class TaskFinished(id:EventId, name: String,
                        postTime: ModellingTimesatmp,
                        eventTime: ModellingTimesatmp,
                        task: Task,
                        node: Node) extends Event
