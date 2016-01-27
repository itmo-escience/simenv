package itmo.escience.simenv.experiments

import itmo.escience.simenv.algorithms.{RandomScheduler, Scheduler}
import itmo.escience.simenv.algorithms.ga.{CGAScheduler, GAScheduler}
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl._
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.utilities.Utilities._
import itmo.escience.simenv.utilities.Units._

/**
  * Created by mikhail on 21.01.2016.
  */
class CGACloudCostOptimization extends Experiment {
  var ctx: Context[DaxTask, CapacityBasedNode] = null
  var env: Environment[CapacityBasedNode] = null
  var scheduler: Scheduler = null

  def init(): Unit = {
    val basepath = ".\\resources\\wf-examples\\"
//    val basepath = ".\\resources\\"
//    val wf_name = "crawlerWf"
      val wf_name = "Montage_25"
    val wf1 = parseDAX(basepath + wf_name + ".xml", "M1", 200)
//    val wf2 = parseDAX(basepath + wf_name + ".xml", "M2", 200)
//    val wf3 = parseDAX(basepath + wf_name + ".xml", "M3", 200)
//    val wf4 = parseDAX(basepath + wf_name + ".xml", "M4", 200)
    val workload = new MultiWfWorkload(List(wf1))

    val idealCapacity: Double = 20
    val nodeTypes: List[Double] = List(10, 15, 20, 25)

    var nodes = List[CapacityBasedNode]()
    for (i <- 0 until 4) {
      val res: CapacityBasedNode = new CapacityBasedNode(id=s"res_$i", name=s"res_$i",
        capacity = nodeTypes(i))
      nodes :+= res
    }

    val bandwidth = 100 Mbit_Sec
    val globalNet = new Network(id=generateId(), name="global net", bandwidth=bandwidth, nodes)
//    val local1 = new Network(id=generateId(), name="local", bandwidth=bandwidth*10, nodes.take(2))
//    val local2 = new Network(id=generateId(), name="local", bandwidth=bandwidth*10, List(nodes(2), nodes(3)))
//    val local3 = new Network(id=generateId(), name="local", bandwidth=bandwidth*10, nodes.drop(4))

    val networks = List(globalNet)

    env = new BasicEnvironment(nodes, networks, nodeTypes)
    val estimator = new BasicEstimator[CapacityBasedNode](idealCapacity, env)

    scheduler = new CGAScheduler(crossoverProb=0.4,
      mutationProb=0.2,
      swapMutationProb=0.3,
      popSize=20,
      iterationCount=10)

    ctx = new BasicContext[DaxTask, CapacityBasedNode](env, Schedule.emptySchedule(),
      estimator, 0.0, workload)
  }

  override def run(): Unit = {

    val random_schedule = RandomScheduler.schedule[DaxTask, CapacityBasedNode](ctx, env)
    println(s"Random makespan: ${random_schedule.makespan()}")

    println("Init environment:")
    println(env.envPrint())
    val ga_schedule = scheduler.schedule(ctx, env)
    println("GA SCHEDULE:")
//    println(ga_schedule.prettyPrint())
    println(s"GA makespan: ${ga_schedule.makespan()}")
    println("Finished")
  }

}
