package itmo.escience.simenv

import itmo.escience.simenv.algorithms.RandomScheduler
import itmo.escience.simenv.algorithms.ga.GAScheduler
import itmo.escience.simenv.algorithms.ga.cga.CoevGAScheduler
import itmo.escience.simenv.algorithms.ga.vmga.GAEnvConfigurator
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl._
import itmo.escience.simenv.simulator.VmBasedSimulator
import itmo.escience.simenv.simulator.events.EventQueue
import itmo.escience.simenv.utilities.ScheduleHelper
import itmo.escience.simenv.utilities.Utilities._
import org.junit.Test

/**
  * Created by Mishanya on 14.12.2015.
  */
@Test
class StaticConfigurationTest {
  val basepath = ".\\resources\\wf-examples\\"
  //  val wfs = List("Montage_25", "Montage_30", "Montage_75", "Montage_100",
  //      "CyberShake_30", "CyberShake_50", "CyberShake_75", "CyberShake_100",
  //      "Inspiral_30", "Inspiral_50", "Inspiral_72", "Inspiral_100"
  //    ).map(x => basepath + x + ".xml" ).map(x => parseDAX(x))

  val wfs = List("Montage_30"//, "Montage_30", "Montage_75", "Montage_100",
    //"CyberShake_30", "CyberShake_50", "CyberShake_75", "CyberShake_100",
    //"Inspiral_30", "Inspiral_50", "Inspiral_72", "Inspiral_100"
  ).map(x => basepath + x + ".xml" ).map(x => parseDAX(x))
  val wf = wfs(0)

  val res1: PhysicalResource = new PhysicalResource(id=generateId(), name="res1",
    cores=16, ram=16,
    storage=new SimpleStorage(id=generateId(), name="storage1", volume=1024),
    reliability=0.95
    )
  res1.runVM(12, 12, 512)
  res1.runVM(4, 4, 256)

  val res2: PhysicalResource = new PhysicalResource(id=generateId(), name="res2",
    cores=16, ram=16,
    storage=new SimpleStorage(id=generateId(), name="storage2", volume=1024),
    reliability=0.95
  )
  res2.runVM(14, 14, 512)
  res2.runVM(2, 2, 256)

  val nodes = List(res1, res2)

  val Mb_sec_100 = 1024*1024*100/8

  val networks = List(new Network(id=generateId(), name="", bandwidth=Mb_sec_100, nodes))

  val environment = new PhysResourceEnvironment(nodes, networks)
  val estimator = new PhysEnvEstimator(environment)

  @Test
  def testDynamic() = {
//    val scheduler = RandomScheduler
    val scheduler = new GAScheduler(crossoverProb = 0.4,
      mutationProb = 0.2,
      swapMutationProb = 0.3,
      popSize = 50,
      iterationCount = 50)
    val configurator = new GAEnvConfigurator(crossoverProb = 0.4,
      mutationProb = 0.3,
      popSize = 50,
      iterationCount = 50)

    val ctx = new BasicContext[DaxTask, CoreRamHddBasedNode](environment, Schedule.emptySchedule(),
      estimator, 0.0, new SingleAppWorkload(wf))

    val sched = scheduler.schedule(ctx, ctx.environment)
    println("Schedule makespan = " + sched.makespan())

    ctx.applySchedule(sched, new EventQueue())
    val env = configurator.environmentConfig(ctx, sched)

    ScheduleHelper.checkStaticSchedule(ctx)
    println(ctx.schedule.prettyPrint())
    println(env.asInstanceOf[PhysResourceEnvironment].vms.map(x => s"${x.cores}; ${x.ram}"))
    print("Finished")
  }
}
