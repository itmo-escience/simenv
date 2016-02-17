package itmo.escience.simenv.algorithms.ga.quality

import itmo.escience.simenv.environment.entities.{NodeId, Task, TaskId}

import scala.collection.parallel.Tasks

/**
 * Created by user on 17.02.2016.
 */
class QBScheduleSolution(val tasksImpl: List[(TaskId, Task)],
                         val taskOrdering: List[TaskId],
                         val taskMapping: List[(TaskId, NodeId)])
