package itmo.escience.simenv

import itmo.escience.simenv.algorithms.ga.GAScheduler
import itmo.escience.simenv.environment.entities.{Schedule, DaxTask, Network, CapacityBasedNode}
import itmo.escience.simenv.environment.entitiesimpl.{SingleAppWorkload, BasicContext, BasicEstimator, BasicEnvironment}
import itmo.escience.simenv.simulator.SimpleSimulator
import itmo.escience.simenv.utilities.ScheduleHelper
import itmo.escience.simenv.utilities.Utilities._
import org.junit.Test

/**
  * Created by Mishanya on 14.12.2015.
  */
@Test
class DynamicSchedulingTest {
  val basepath = ".\\resources\\wf-examples\\"
  //  val wfs = List("Montage_25", "Montage_30", "Montage_75", "Montage_100",
  //      "CyberShake_30", "CyberShake_50", "CyberShake_75", "CyberShake_100",
  //      "Inspiral_30", "Inspiral_50", "Inspiral_72", "Inspiral_100"
  //    ).map(x => basepath + x + ".xml" ).map(x => parseDAX(x))

  val wfs = List("Montage_100"//, "Montage_30", "Montage_75", "Montage_100",
    //"CyberShake_30", "CyberShake_50", "CyberShake_75", "CyberShake_100",
    //"Inspiral_30", "Inspiral_50", "Inspiral_72", "Inspiral_100"
  ).map(x => basepath + x + ".xml" ).map(x => parseDAX(x))
  val wf = wfs(0)


  val nodes = List(new CapacityBasedNode(id=generateId(), name="", nominalCapacity=30, reliability=0.95),
    new CapacityBasedNode(id=generateId(), name="", nominalCapacity=25, reliability=0.95),
    new CapacityBasedNode(id=generateId(), name="", nominalCapacity=15, reliability=0.95),
    new CapacityBasedNode(id=generateId(), name="", nominalCapacity=10, reliability=0.95))

  val Mb_sec_100 = 1024*1024*100/8

  val networks = List(new Network(id=generateId(), name="", bandwidth=Mb_sec_100, nodes))

  val environment = new BasicEnvironment(nodes, networks)
  val estimator = new BasicEstimator(idealCapacity = 20.0, environment)

  @Test
  def testDynamic() = {
    val scheduler = new GAScheduler(crossoverProb = 0.4,
      mutationProb = 0.2,
      swapMutationProb = 0.3,
      popSize = 50,
      iterationCount = 100)

    var ctx = new BasicContext[DaxTask, CapacityBasedNode](environment, Schedule.emptySchedule(),
      estimator, 0.0, new SingleAppWorkload(wf))

    val simulator = new SimpleSimulator(scheduler, ctx)
    simulator.init()
    simulator.runSimulation()

//    val schedule = scheduler.schedule(ctx)
//    ctx.schedule = schedule

    ScheduleHelper.checkStaticSchedule(ctx)
    print("Finished")
  }
}
