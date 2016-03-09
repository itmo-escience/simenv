package itmo.escience.simenv.experiments

import itmo.escience.simenv.algorithms.HEFTScheduler
import itmo.escience.simenv.algorithms.ga.CGAScheduler
import itmo.escience.simenv.simulator.SchedConfSimulator

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl._
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.utilities.Utilities._

/**
  * Created by mikhail on 29.01.2016.
  */
class CGADynamExp(wfPath: String, envArray: List[List[Double]], globNet: Double, locNet: Double, reliability: Double,
                  nodeResizeTime: Double, nodeDownTime: Double, resDownTime: Double) extends Experiment{
  val wf = parseDAX(wfPath + ".xml")

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



  override def run(): Double = {
//    println("Init environment:")

    val scheduler = new CGAScheduler(crossoverProb = 0.4,
      mutationProb = 0.3,
      swapMutationProb = 0.3,
      popSize = 50,
      iterationCount = 50)

    val ctx = new BasicContext[DaxTask, CapacityBasedNode](environment, Schedule.emptySchedule[DaxTask, CapacityBasedNode](),
      estimator, 0.0, new SingleAppWorkload(wf))

    val simulator = new SchedConfSimulator(scheduler, ctx, nodeDownTime, resDownTime, nodeResizeTime)
    simulator.init()
    simulator.runSimulation()
    print("Makespan:")
    println(ctx.schedule.makespan())
//    println(ctx.schedule.prettyPrint())

    println("CGA env: ")
    println(ctx.environment.asInstanceOf[CarrierNodeEnvironment[CapacityBasedNode]].envPrint())

    ctx.schedule.makespan()
  }
}
