package itmo.escience.simenv.experiments

import itmo.escience.simenv.algorithms.ga.GAScheduler
import itmo.escience.simenv.algorithms.{Scheduler, HEFTScheduler, MinMinScheduler}
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.{SingleAppWorkload, BasicContext, CpuTimeEstimator, CarrierNodeEnvironment}
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.utilities.Utilities._
import itmo.escience.simenv.utilities.Units._

/**
  * Created by Mishanya on 18.01.2016.
  */
class GAStaticScheduling extends Experiment {
  var ctx: Context[DaxTask, CpuTimeNode] = null
  var env: Environment[CpuTimeNode] = null
  var scheduler: Scheduler[DaxTask, Node] = null

  def init(): Unit = {
    val basepath = ".\\resources\\"
    val wf_name = "crawlerWf"
    //  val wf_name = "Montage_25"
    val wf = parseDAX(basepath + wf_name + ".xml")

    var nodes = List[Node]()
    for (i <- 0 until 4) {
      val res: CpuTimeCarrier = new CpuTimeCarrier(id=s"res_$i", name=s"res_$i",
        cores=4)
      for (j <- 0 until 2) {
        val node: CpuTimeNode = new CpuTimeNode(id = s"res_${i}_node_$j", name = s"res_${i}_node_$j",
          cores = 2, cpuTime = 50, parent = res.id)
        res.addChild(node)
      }
      nodes :+= res
    }

    val bandwidth = 100 Mbit_Sec
    val globalNet = new Network(id=generateId(), name="global net", bandwidth=bandwidth, nodes)
    val local1 = new Network(id=generateId(), name="local", bandwidth=bandwidth*10, nodes.take(2))
    val local2 = new Network(id=generateId(), name="local", bandwidth=bandwidth*10, List(nodes(2), nodes(3)))
    val local3 = new Network(id=generateId(), name="local", bandwidth=bandwidth*10, nodes.drop(4))

    val networks = List(globalNet, local1, local2, local3)

    env = new CarrierNodeEnvironment[CpuTimeNode](nodes, networks)
    val estimator = new CpuTimeEstimator(env)

    scheduler = new GAScheduler(crossoverProb=0.4,
                  mutationProb=0.2,
                  swapMutationProb=0.3,
                  popSize=50,
                  iterationCount=300)

    ctx = new BasicContext[DaxTask, CpuTimeNode](env, Schedule.emptySchedule(),
        estimator, 0.0, new SingleAppWorkload(wf))
  }

  override def run(): Unit = {
    println("Init environment:")
    println(env.asInstanceOf[CarrierNodeEnvironment[CpuTimeNode]].envPrint())
    val ga_schedule = scheduler.schedule(ctx.asInstanceOf[Context[DaxTask, Node]], env.asInstanceOf[Environment[Node]])
    println("GA SCHEDULE:")
    println(ga_schedule.prettyPrint())
    println(s"GA makespan: ${ga_schedule.makespan()}")
    println("Finished")
  }
}
