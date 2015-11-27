package itmo.escience.simulator.events

import itmo.escience.environment.entities.{ModellingTimesatmp, Task, Node}
import itmo.escience.simulator.EventId

/**
 * Created by Mishanya on 15.10.2015.
 */
case class TaskFinished(id:EventId, name: String,
                        postTime: ModellingTimesatmp,
                        eventTime: ModellingTimesatmp,
                        task: Task,
                        node: Node) extends Event
