package itmo.escience.Experiments

import itmo.escience.Environment.Entities.{Node, Task}

/**
 * Created by Mishanya on 14.10.2015.
 */
trait Experiment {
  def runExperiment(tasks: List[Task], nodes: List[Node]): Unit
}
