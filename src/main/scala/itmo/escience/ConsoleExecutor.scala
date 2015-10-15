package itmo.escience

import itmo.escience.Environment.Entities.{Node, Task}
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
  tasks ::= new Task("t_0", 30)
  tasks ::= new Task("t_1", 10)
  tasks ::= new Task("t_2", 15)
  tasks ::= new Task("t_3", 20)
  tasks ::= new Task("t_4", 35)
  // Set nodes scenario
  var nodes: List[Node] = List()
  nodes ::= new Node("n_0", 10)
  nodes ::= new Node("n_1", 15)

  // Init experiments
  val exp: Experiment = new TestExperiment()

  // Run experiments with tasks and nodes
  exp.runExperiment(tasks, nodes)
}
