//package itmo.escience.simenv
//
//import itmo.escience.simenv.algorithms.ga.WorkflowSchedulingProblem
//import itmo.escience.simenv.algorithms.{RandomScheduler, Scheduler}
//import itmo.escience.simenv.environment.entities.{Schedule, DaxTask, Network, CapacityBasedNode}
//import itmo.escience.simenv.environment.entitiesimpl.{SingleAppWorkload, BasicContext, BasicEstimator, BasicEnvironment}
//import itmo.escience.simenv.utilities.ScheduleHelper
//import itmo.escience.simenv.utilities.Utilities._
//import org.junit.Test
//
///**
// * Created by user on 02.12.2015.
// */
//@Test
//class SolutionToScheduleTest {
//
//  val basepath = ".\\resources\\wf-examples\\"
//  val wfs = List("Montage_25", "Montage_30", "Montage_75", "Montage_100",
//    "CyberShake_30", "CyberShake_50", "CyberShake_75", "CyberShake_100",
//    "Inspiral_30", "Inspiral_50", "Inspiral_72", "Inspiral_100"
//  ).map(x => basepath + x + ".xml" ).map(x => parseDAX(x))
//
//  val nodes = List(new CapacityBasedNode(id=generateId(), name="", nominalCapacity=30),
//    new CapacityBasedNode(id=generateId(), name="", nominalCapacity=25),
//    new CapacityBasedNode(id=generateId(), name="", nominalCapacity=15),
//    new CapacityBasedNode(id=generateId(), name="", nominalCapacity=10))
//
//  val Mb_sec_100 = 1024*1024*100/8
//
//  val networks = List(new Network(id=generateId(), name="", bandwidth=Mb_sec_100, nodes))
//
//  val environment = new BasicEnvironment(nodes, networks)
//  val estimator = new BasicEstimator(idealCapacity = 20.0, environment)
//
////  @Test
////  def testSolutionToScheduleTransformations() = {
////    for (wf <- wfs){
////      val ctx = new BasicContext[DaxTask, CapacityBasedNode](environment, Schedule.emptySchedule(),
////        estimator, 0.0, new SingleAppWorkload(wf))
////      val schedule = RandomScheduler.schedule(ctx)
////
////      val solution = WorkflowSchedulingProblem.scheduleToSolution(schedule, ctx)
////      val backSchedule = WorkflowSchedulingProblem.solutionToSchedule(solution, ctx)
////
////      ScheduleHelper.checkStaticSchedule(wf, schedule, ctx, haveToBeFinished = false)
////      ScheduleHelper.checkStaticSchedule(wf, backSchedule, ctx, haveToBeFinished = false)
////
////      println(s"Makespan before: ${schedule.makespan()} and after: ${backSchedule.makespan()}")
////    }
////  }
//
//  private def runOnWfs() = {
//
//  }
//
//}
