package itmo.escience.Experiments

import itmo.escience.Environment.Entities.{Node, Task}
import itmo.escience.Environment.{Workload, ResourceManager}

/**
 * Created by Mishanya on 14.10.2015.
 */
trait Experiment {
  def runExperiment(workload: Workload, resourceManager: ResourceManager): Unit
}
