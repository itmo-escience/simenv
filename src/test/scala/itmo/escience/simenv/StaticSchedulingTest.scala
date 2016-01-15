//package itmo.escience.simenv
//
//import itmo.escience.simenv.algorithms.ga.GAScheduler
//import itmo.escience.simenv.algorithms.{RandomScheduler, Scheduler, HEFTScheduler, MinMinScheduler}
//import itmo.escience.simenv.environment.entities._
//import itmo.escience.simenv.environment.entitiesimpl.{SingleAppWorkload, BasicEstimator, BasicEnvironment, BasicContext}
//import itmo.escience.simenv.environment.modelling.{Workload, Estimator, Environment}
//import itmo.escience.simenv.utilities.ScheduleHelper
//import itmo.escience.simenv.utilities.Utilities._
//import org.junit.{Before, Test}
//import org.junit.Assert._
//
///**
// * Created by Nikolay on 11/29/2015.
// */
//@Test
//class StaticSchedulingTest {
//
//  val basepath = "D:\\wspace\\simenv\\resources\\wf-examples\\"
//  val wfs = List("Montage_25", "Montage_30", "Montage_75", "Montage_100",
//      "CyberShake_30", "CyberShake_50", "CyberShake_75", "CyberShake_100",
//      "Inspiral_30", "Inspiral_50", "Inspiral_72", "Inspiral_100"
//    ).map(x => basepath + x + ".xml" ).map(x => parseDAX(x))
//
////  val wfs = List("Montage_25" ).map(x => basepath + x + ".xml" ).map(x => parseDAX(x))
//
//  val nodes = List(new CapacityBasedNode(id=generateId(), name="", nominalCapacity=30),
//      new CapacityBasedNode(id=generateId(), name="", nominalCapacity=25),
//      new CapacityBasedNode(id=generateId(), name="", nominalCapacity=15),
//      new CapacityBasedNode(id=generateId(), name="", nominalCapacity=10))
//
//  val Mb_sec_100 = 1024*1024*100/8
//
//  val networks = List(new Network(id=generateId(), name="", bandwidth=Mb_sec_100, nodes))
//
//  val environment = new BasicEnvironment(nodes, networks)
//  val estimator = new BasicEstimator(idealCapacity = 20.0, environment)
//
// //@Test
////  def testMinMinScheduler() = {
////    runOnWfs(MinMinScheduler, "MinMin")
////  }
////
////  //@Test
////  def testHEFTScheduler() = {
////    runOnWfs(HEFTScheduler, "HEFT")
////  }
////
////  //@Test
////  def testRandomScheduler() = {
////    runOnWfs(RandomScheduler, "Random")
////  }
////
////  @Test
////  def testGAScheduler() = {
////    runOnWfs(new GAScheduler(crossoverProb = 0.4,
////      mutationProb = 0.2,
////      swapMutationProb = 0.3,
////      popSize = 50,
////      iterationCount = 100), "GA")
////  }
//
//  private def runOnWfs(scheduler:Scheduler[DaxTask, CapacityBasedNode], schedulerName: String) = {
//    for (wf <- wfs){
//      val ctx = new BasicContext[DaxTask, CapacityBasedNode](environment, Schedule.emptySchedule(),
//        estimator, 0.0, new SingleAppWorkload(wf))
//      val schedule = scheduler.schedule(ctx, ctx.environment)
//      ctx.schedule = schedule
//
//      ScheduleHelper.checkStaticSchedule(ctx)
//
//      println(s"Workflow ${wf.name} has been successfully scheduled by ${schedulerName} - makespan: ${ctx.schedule.makespan()}")
//    }
//  }
//}
