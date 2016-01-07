package itmo.escience.simenv.algorithms.ga.vmga

import itmo.escience.simenv.environment.entities.{NodeId, TaskId}

/**
 * gene for genetic algorithm
 */
case class MappedVm(vmId: NodeId, config: (Int, Int))
