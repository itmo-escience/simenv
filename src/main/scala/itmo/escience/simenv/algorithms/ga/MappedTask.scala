package itmo.escience.simenv.algorithms.ga

import itmo.escience.simenv.environment.entities.{TaskId, NodeId}

/**
 * gene for genetic algorithm
 */
case class MappedTask(taskId: TaskId, nodeId: NodeId)
