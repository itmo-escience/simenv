package itmo.escience.Executors

import itmo.escience.Environment.Entities._
import itmo.escience.Environment.Estimators.SimpleEstimator
import itmo.escience.Environment.ResourceManagers.SimpleResourceManager
import itmo.escience.Environment.{Environment, Estimator, ResourceManager, Workload}
import itmo.escience.Environment.Workloads.ListOfTasks
import itmo.escience.Executors.Executor
import itmo.escience.Experiments.{Experiment, TestExperiment}

/**
 * Created by Mishanya on 14.10.2015.
 */

/* Standard executing
 */
class ConsoleExecutor extends Executor {
  //TODO design how and where initialize tasks and nodes sets

  // Set workload
  var workload: Workload = new ListOfTasks()

  var tasks: List[Task] = List()
//  tasks :+= new Task("t_0", 30, List(new DataFile("in0", 50)), List(new DataFile("f0", 10)))
//  tasks :+= new Task("t_1", 10, List(new DataFile("in1", 30)), List(new DataFile("f1", 20)))
//  tasks :+= new Task("t_2", 15, List(new DataFile("in2", 40)), List(new DataFile("f2", 15)))
//  tasks :+= new Task("t_3", 20, List(), List(new DataFile("f3", 25)))
//  tasks :+= new Task("t_4", 35, List(), List(new DataFile("f4", 30)))
//  tasks :+= new Task("t_5", 25, List(), List(new DataFile("f5", 15)))

  workload.setTasks(tasks)


  // Set resource manager
  var resourceManager: ResourceManager = new SimpleResourceManager()

  var nodes: List[Node] = List()
  nodes :+= new Node("n_0", 10, new Storage("s_0", 1000), 0.8)
  nodes :+= new Node("n_1", 15, new Storage("s_1", 1000), 0.8)

  resourceManager.setNodes(nodes)


  // Set estimator
  var estimator: Estimator = new SimpleEstimator()


  // Set environment
  var environment: Environment = new Environment
  environment.setEstimator(estimator)
  environment.setResourceManager(resourceManager)



  // Init experiments
  val exp: Experiment = new TestExperiment()

  // Run experiments with tasks and nodes
  exp.runExperiment(workload, environment)
}
