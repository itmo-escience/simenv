package itmo.escience.simenv

import itmo.escience.simenv.algorithms.ga.cga.CoevGAScheduler
import itmo.escience.simenv.algorithms.ga.vmga.GAEnvConfigurator
import itmo.escience.simenv.algorithms.{HEFTScheduler, MinMinScheduler}
import itmo.escience.simenv.algorithms.ga.GAScheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl._
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.simulator.events.EventQueue
import itmo.escience.simenv.utilities.ScheduleHelper
import itmo.escience.simenv.utilities.Units._
import itmo.escience.simenv.utilities.Utilities._
import org.junit.Test

/**
  * Created by Mishanya on 14.12.2015.
  */
@Test
class StaticCrawlerExecutionTest {
//  val basepath = ".\\resources\\wf-examples\\"
  val basepath = ".\\resources\\"
  val wf_name = "crawlerWf"
  val wf = parseDAX(basepath + wf_name + ".xml")

  var nodes = List[Node]()
  for (i <- 0 until 9) {
    val res: CpuTimeCarrier = new CpuTimeCarrier(id=s"res_$i", name=s"res_$i",
      cpu=4)
//    val node: CpuTimeNode = new CpuTimeNode(id=s"res_${i}_node_0", name=s"res_${i}_node_0",
//      cpu=4, cpuTime=100, parent=res.id)
    for (j <- 0 until 2) {
      val node: CpuTimeNode = new CpuTimeNode(id = s"res_${i}_node_$j", name = s"res_${i}_node_$j",
        cpu = 4, cpuTime = 50, parent = res.id)
      res.addChild(node)
    }
    nodes :+= res
  }

//  val Mb_sec_100 = 1024*1024*100/8
  val Mb_sec_100 = 100 Mbit_Sec
//  val bandwidth = 1000 так в 2 раза выигрыш
  val bandwidth = Mb_sec_100 //1000
  val globalNet = new Network(id=generateId(), name="global net", bandwidth=bandwidth, nodes)
  val local1 = new Network(id=generateId(), name="local", bandwidth=bandwidth*10, nodes.take(2))
  val local2 = new Network(id=generateId(), name="local", bandwidth=bandwidth*10, List(nodes(2), nodes(3)))
  val local3 = new Network(id=generateId(), name="local", bandwidth=bandwidth*10, nodes.drop(4))

  val networks = List(globalNet, local1, local2, local3)

  val environment: Environment[CpuTimeNode] = new CarrierNodeEnvironment[CpuTimeNode](nodes, networks)
  val estimator = new CpuTimeEstimator(environment)

//  println("Init environment:")
//  println(environment.asInstanceOf[CarrierNodeEnvironment[CpuTimeNode]].envPrint())

  @Test
  def testExperiment() = {
    val scheduler = new GAScheduler(crossoverProb=0.4,
            mutationProb=0.2,
            swapMutationProb=0.3,
            popSize=50,
            iterationCount=300)

    val configurator = new GAEnvConfigurator(crossoverProb=0.4,
      mutationProb=0.2,
      popSize=50,
      iterationCount=100)

    val coevScheduler = new CoevGAScheduler(crossoverProb = 0.4,
      mutationProb = 0.2,
      swapMutationProb = 0.3,
      popSize = 50,
      iterationCount = 100,
      vmMutationProb = 0.2,
      vmCrossoverProb = 0.4,
      vmPopSize = 50,
      vmIterationCount = 300,
      coevCycles = 4)
//
    val ctx = new BasicContext[DaxTask, CpuTimeNode](environment, Schedule.emptySchedule(),
      estimator, 0.0, new SingleAppWorkload(wf))
//
//    val ga_schedule = scheduler.schedule(ctx.asInstanceOf[Context[DaxTask, Node]], environment.asInstanceOf[Environment[Node]])
    val minmin_schedule = MinMinScheduler.schedule(ctx.asInstanceOf[Context[DaxTask, Node]], environment.asInstanceOf[Environment[Node]])
    val heft_schedule = HEFTScheduler.schedule(ctx.asInstanceOf[Context[DaxTask, Node]], environment.asInstanceOf[Environment[Node]])

//    println(ga_schedule.prettyPrint())
//    println(s"GA makespan: ${ga_schedule.makespan()}")
    println("_________")
//    println(minmin_schedule.prettyPrint())
    println(s"MinMin makespan: ${minmin_schedule.makespan()}")
    println("_________")
    println(s"HEFT makespan: ${heft_schedule.makespan()}")
    println("Schedule after Heft")
    println(heft_schedule.prettyPrint())
    heft_schedule.prettyPrint()

//    println("________")
//    println("Configuration")
//    val ga_environment = configurator.environmentConfig(ctx, minmin_schedule)
//    println("minmin sched, ga config:")
//    println(ga_environment.envPrint())

    println("________")
    val (coev_schedule, coev_env) = coevScheduler.scheduleAndConfiguration(ctx.asInstanceOf[Context[DaxTask, Node]], environment.asInstanceOf[Environment[Node]])

    println("Schedule after coevolution")
    println(coev_schedule.prettyPrint())
    println(coev_env.envPrint())
    println(s"coev makespan: ${coev_schedule.makespan()}")

    println("Finished")

    println(coev_schedule.makespan() / minmin_schedule.makespan() )
  }
}
