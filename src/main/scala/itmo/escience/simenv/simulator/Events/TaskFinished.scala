package itmo.escience.simenv.simulator.Events

import itmo.escience.environment.entities.{ModellingTimesatmp, DaxTask, Node}
import itmo.escience.simulator.EventId

/**
 * Created by Mishanya on 15.10.2015.
 */
case class TaskFinished(id:EventId, name: String,
                        postTime: ModellingTimesatmp,
                        eventTime: ModellingTimesatmp,
                        task: DaxTask,
                        node: Node) extends Event
