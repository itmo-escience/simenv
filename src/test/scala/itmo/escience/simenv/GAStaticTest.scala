package itmo.escience.simenv

import itmo.escience.simenv.algorithms.ga.GAScheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl._
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.simulator.BasicSimulator
import itmo.escience.simenv.utilities.Units._
import itmo.escience.simenv.utilities.Utilities._
import org.junit.Test

/**
  * Created by Mishanya on 14.12.2015.
  */
@Test
class GAStaticTest {
val wfPath = ".\\resources\\wf-examples\\Montage_25"

  val wf = parseDAX(wfPath + ".xml")

  val envArray = List(List(10.0, 10.0, 20.0), List(10.0, 10.0, 20.0))

  val globNet = 10 Mbit_Sec
  val locNet = 1000 Mbit_Sec
  val reliability = 1
  val nodeDownTime = 5
  val resDownTime = 10

  var networks = List[Network]()

  var nodes = List[Node]()
  for ((l, i) <- envArray.zipWithIndex) {
    val res: CapacityBasedCarrier = new CapacityBasedCarrier(id=s"res_$i", name=s"res_$i",
      capacity=l.sum, reliability = reliability)

    for ((l2, j) <- l.zipWithIndex) {
      val node: CapacityBasedNode = new CapacityBasedNode(id = s"res_${i}_node_$j", name = s"res_${i}_node_$j",
        capacity = l2, parent = res.id, reliability = reliability)
      res.addChild(node)
    }
    val localNet = new Network(id=generateId(), name="local net", bandwidth=locNet, res.children)
    networks :+= localNet
    nodes :+= res
  }


  val globalNet = new Network(id=generateId(), name="global net", bandwidth=globNet, nodes)
  networks :+= globalNet

  val environment: Environment[CapacityBasedNode] = new CarrierNodeEnvironment[CapacityBasedNode](nodes, networks)
  val estimator = new BasicEstimator[CapacityBasedNode](20, environment)


  @Test
  def testExperiment() = {
    val scheduler = new GAScheduler(crossoverProb = 0.4,
      mutationProb = 0.3,
      swapMutationProb = 0.3,
      popSize = 10,
      iterationCount = 10)

    val ctx = new BasicContext[DaxTask, CapacityBasedNode](environment, Schedule.emptySchedule[DaxTask, CapacityBasedNode](),
      estimator, 0.0, new SingleAppWorkload(wf))

    val sched = scheduler.schedule(ctx, environment)
  }
}
