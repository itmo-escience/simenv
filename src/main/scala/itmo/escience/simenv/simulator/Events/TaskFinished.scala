package itmo.escience.simenv.simulator.events

import itmo.escience.simenv.environment.entities.{ModellingTimestamp, Node, Task}
import itmo.escience.simenv.simulator.EventId

/**
 * Created by Mishanya on 15.10.2015.
 */
case class TaskFinished(id:EventId, name: String,
                        postTime: ModellingTimestamp,
                        eventTime: ModellingTimestamp,
                        task: Task,
                        node: Node) extends Event
