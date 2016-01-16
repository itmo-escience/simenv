package itmo.escience.simenv

import itmo.escience.simenv.algorithms.ga.cga.CoevGAScheduler
import itmo.escience.simenv.algorithms.{Scheduler, HEFTScheduler, MinMinScheduler}
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl._
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.simulator.SchedConfSimulator
import itmo.escience.simenv.utilities.Utilities._
import org.junit.Test

/**
  * Created by Mishanya on 14.12.2015.
  */
@Test
class DynamicSchedConfExecutionTest {
  val basepath = ".\\resources\\wf-examples\\"
//  val basepath = ".\\resources\\"
//  val wf_name = "crawlerWf"
  val wf_name = "Montage_25"
  val wf = parseDAX(basepath + wf_name + ".xml")

  var nodes = List[Node]()
  for (i <- 0 until 9) {
    val res: CpuTimeCarrier = new CpuTimeCarrier(id=s"res_$i", name=s"res_$i",
      cores=4, reliability = 0.95)
    for (j <- 0 until 2) {
      val node: CpuTimeNode = new CpuTimeNode(id = s"res_${i}_node_$j", name = s"res_${i}_node_$j",
        cores = 4, cpuTime = 50, parent = res.id, reliability = 0.95)
      res.addChild(node)
    }
    nodes :+= res
  }

  val Mb_sec_100 = 1024*1024*100/8
//  val Mb_sec_100 = 1024*1024/8 * 100
//  val bandwidth = 1000 так в 2 раза выигрыш
  val bandwidth = Mb_sec_100 //1000
  val globalNet = new Network(id=generateId(), name="global net", bandwidth=bandwidth, nodes)
  val local1 = new Network(id=generateId(), name="local", bandwidth=bandwidth*10, nodes.take(2))
  val local2 = new Network(id=generateId(), name="local", bandwidth=bandwidth*10, List(nodes(2), nodes(3)))
  val local3 = new Network(id=generateId(), name="local", bandwidth=bandwidth*10, nodes.drop(4))

  val networks = List(globalNet, local1, local2, local3)

  val environment: Environment[CpuTimeNode] = new CarrierNodeEnvironment[CpuTimeNode](nodes, networks)
  val estimator = new CpuTimeEstimator(environment)

  @Test
  def testExperiment() = {
    println("Init environment:")
    println(environment.asInstanceOf[CarrierNodeEnvironment[CpuTimeNode]].envPrint())

    val coevScheduler = new CoevGAScheduler(crossoverProb = 0.4,
      mutationProb = 0.2,
      swapMutationProb = 0.3,
      popSize = 50,
      iterationCount = 50,
      vmMutationProb = 0.2,
      vmCrossoverProb = 0.4,
      vmPopSize = 50,
      vmIterationCount = 50,
      coevCycles = 4)
//
    val ctx = new BasicContext[DaxTask, CpuTimeNode](environment, Schedule.emptySchedule(),
      estimator, 0.0, new SingleAppWorkload(wf))

    val simulator = new SchedConfSimulator(coevScheduler.asInstanceOf[Scheduler[DaxTask, CpuTimeNode]], ctx)
        simulator.init()
        simulator.runSimulation()

    println("Result environment:")
    println(ctx.environment.asInstanceOf[CarrierNodeEnvironment[CpuTimeNode]].envPrint())

    println("Finished")
  }
}
