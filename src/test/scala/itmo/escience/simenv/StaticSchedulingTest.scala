package itmo.escience.simenv

import itmo.escience.simenv.algorithms.MinMinScheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.{SingleAppWorkload, BasicEstimator, BasicEnvironment, BasicContext}
import itmo.escience.simenv.environment.modelling.{Workload, Estimator, Environment}
import itmo.escience.simenv.utilities.ScheduleHelper
import itmo.escience.simenv.utilities.Utilities._
import org.junit.Test

/**
 * Created by Nikolay on 11/29/2015.
 */
@Test
class StaticSchedulingTest {

  @Test
  def testMinMinScheduler() = {

    val basepath = "D:\\wspace\\simenv\\resources\\wf-examples\\"
    val pathWfs = List( "Montage_25").map(x => basepath + x + ".xml" )


    //construct environment
    val nodes = List(new CapacityBasedNode(id=generateId(), name="", nominalCapacity=30),
      new CapacityBasedNode(id=generateId(), name="", nominalCapacity=25),
      new CapacityBasedNode(id=generateId(), name="", nominalCapacity=15),
      new CapacityBasedNode(id=generateId(), name="", nominalCapacity=10))

    val MB_sec_100 = 1024*1024*100

    val networks = List(new Network(id=generateId(), name="", bandwidth=MB_sec_100, nodes))

    val environment = new BasicEnvironment(nodes, networks)
    val estimator = new BasicEstimator(idealCapacity = 20.0, environment)

    for (path <- pathWfs ){
      val wf = parseDAX(path)
      val workload = new SingleAppWorkload(wf)
      val ctx = new BasicContext[DaxTask, CapacityBasedNode](environment, Schedule.emptySchedule(), estimator, 0.0, workload)
      val schedule = MinMinScheduler.schedule(ctx)
      ctx.schedule = schedule

      ScheduleHelper.checkStaticSchedule(ctx)
    }
  }
}
