package itmo.escience.simenv

import itmo.escience.simenv.algorithms.MinMinScheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.{SingleAppWorkload, BasicContext, BasicEstimator, BasicEnvironment}
import itmo.escience.simenv.utilities.Utilities._
import org.junit.Test
import itmo.escience.simenv.simulator.SimpleSimulator

/**
 * Created by Nikolay on 11/29/2015.
 */
@Test
class SimulatorTest {

  @Test
  def testSimulator() = {

    // TODO: actually, single app and multi app workload should be tested separately
    throw new NotImplementedError()

//    val wfs = List("", "",)
//
//    val nodes = List(new CapacityBasedNode(id=generateId(), name="", nominalCapacity=30),
//      new CapacityBasedNode(id=generateId(), name="", nominalCapacity=25),
//      new CapacityBasedNode(id=generateId(), name="", nominalCapacity=15),
//      new CapacityBasedNode(id=generateId(), name="", nominalCapacity=10))
//
//    val MB_sec_100 = 1024*1024*100
//
//    val networks = List(new Network(id=generateId(), name="", bandwidth=MB_sec_100, nodes))
//
//    val environment = new BasicEnvironment(nodes, networks)
//    val estimator = new BasicEstimator(idealCapacity = 20.0, environment)
//    val wf = parseDAX(awf)
//    val workload = new SingleAppWorkload(wf)
//
//    val ctx = new BasicContext[DaxTask, CapacityBasedNode](environment, Schedule.emptySchedule(), estimator, 0.0, workload)
//    val sim = new SimpleSimulator(MinMinScheduler,ctx)
  }
}
