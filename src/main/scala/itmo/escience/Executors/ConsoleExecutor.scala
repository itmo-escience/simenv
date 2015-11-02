package itmo.escience.Executors

import itmo.escience.Environment.Entities.{DataFile, Node, Storage, Task}
import itmo.escience.Experiments.{Experiment, TestExperiment}

/**
 * Created by Mishanya on 14.10.2015.
 */

/* Standard executing
 */
class ConsoleExecutor extends Executor {
  //TODO design how and where initialize tasks and nodes sets
  // Set tasks scenario
  var tasks: List[Task] = List()
  tasks :+= new Task("t_0", 30, List(new DataFile("in0", 50)), List(new DataFile("f0", 10)))
  tasks :+= new Task("t_1", 10, List(new DataFile("in1", 30)), List(new DataFile("f1", 20)))
  tasks :+= new Task("t_2", 15, List(new DataFile("in2", 40)), List(new DataFile("f2", 15)))
  tasks :+= new Task("t_3", 20, List(), List(new DataFile("f3", 25)))
  tasks :+= new Task("t_4", 35, List(), List(new DataFile("f4", 30)))
  tasks :+= new Task("t_5", 25, List(), List(new DataFile("f5", 15)))
  // Set nodes scenario
  var nodes: List[Node] = List()
  nodes :+= new Node("n_0", 10, new Storage("s_0", 1000), 0.8)
  nodes :+= new Node("n_1", 15, new Storage("s_1", 1000), 0.8)

  // Init experiments
  val exp: Experiment = new TestExperiment()

  // Run experiments with tasks and nodes
  exp.runExperiment(tasks, nodes)
}
