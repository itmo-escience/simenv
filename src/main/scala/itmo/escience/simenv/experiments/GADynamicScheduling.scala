package itmo.escience.simenv.experiments

import itmo.escience.simenv.algorithms.Scheduler
import itmo.escience.simenv.algorithms.ga.GAScheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.{BasicContext, CarrierNodeEnvironment, CpuTimeEstimator, SingleAppWorkload}
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.simulator.{BasicSimulator, SchedConfSimulator}
import itmo.escience.simenv.utilities.Units._
import itmo.escience.simenv.utilities.Utilities._

/**
  * Created by Mishanya on 18.01.2016.
  */
class GADynamicScheduling extends Experiment {
  var ctx: Context[DaxTask, CpuTimeNode] = null
  var env: Environment[CpuTimeNode] = null
  var scheduler: Scheduler[DaxTask, Node] = null

  def init(): Unit = {
    val basepath = ".\\resources\\"
    val wf_name = "crawlerWf"
    //  val wf_name = "Montage_25"
    val wf = parseDAX(basepath + wf_name + ".xml")

    val cores = 8
    val res_number = 4
    val vm_number = 4
    val reliability = 0.95
    var nodes = List[Node]()
    for (i <- 0 until res_number) {
      val res: CpuTimeCarrier = new CpuTimeCarrier(id = s"res_$i", name = s"res_$i",
        cores = cores, reliability = reliability)
      for (j <- 0 until vm_number) {
        val node: CpuTimeNode = new CpuTimeNode(id = s"res_${i}_node_$j", name = s"res_${i}_node_$j",
          cores = cores, cpuTime = 100 / vm_number, parent = res.id, reliability = reliability)
        res.addChild(node)
      }
      nodes :+= res
    }

    val bandwidth = 100 Mbit_Sec
    val globalNet = new Network(id = generateId(), name = "global net", bandwidth = bandwidth, nodes)
    val local1 = new Network(id = generateId(), name = "local", bandwidth = bandwidth * 10, nodes.take(2))
    val local2 = new Network(id = generateId(), name = "local", bandwidth = bandwidth * 10, List(nodes(2), nodes(3)))
    val local3 = new Network(id = generateId(), name = "local", bandwidth = bandwidth * 10, nodes.drop(4))

    val networks = List(globalNet, local1, local2, local3)

    env = new CarrierNodeEnvironment[CpuTimeNode](nodes, networks)
    val estimator = new CpuTimeEstimator(env)

    scheduler = new GAScheduler(crossoverProb = 0.4,
      mutationProb = 0.2,
      swapMutationProb = 0.3,
      popSize = 50,
      iterationCount = 300)

    ctx = new BasicContext[DaxTask, CpuTimeNode](env, Schedule.emptySchedule(),
      estimator, 0.0, new SingleAppWorkload(wf))
  }

  override def run(): Unit = {

    val simulator = new BasicSimulator(scheduler.asInstanceOf[Scheduler[DaxTask, CpuTimeNode]], ctx)
    simulator.init()
    simulator.runSimulation()
    println("Finished")
  }
}
