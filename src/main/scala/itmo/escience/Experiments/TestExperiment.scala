package itmo.escience.Experiments

import itmo.escience.Algorithms.{RandomScheduler, Scheduler}
import itmo.escience.Environment.Entities.{Node, Task}
import itmo.escience.Environment.{Environment, ResourceManager, Workload}
import itmo.escience.Executors.Simulator

/**
 * Created by Mishanya on 14.10.2015.
 */
class TestExperiment extends Experiment {

  var schedAlg: Scheduler = _

  // Construct scheduling algorithm
  schedAlg = new RandomScheduler()

  override def runExperiment(workload: Workload, environment: Environment): Unit = {
    Simulator.simulate(workload, environment, schedAlg)
  }
}
