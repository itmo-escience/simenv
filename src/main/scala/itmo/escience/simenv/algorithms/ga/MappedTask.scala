package itmo.escience.simenv.algorithms.ga

import itmo.escience.simenv.environment.entities.{NodeId, TaskId}

/**
 * gene for genetic algorithm
 */
case class MappedTask(taskId: TaskId, nodeId: NodeId)
