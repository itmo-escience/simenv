package itmo.escience.simenv.experiments

import itmo.escience.simenv.algorithms.ga.GAScheduler
import itmo.escience.simenv.algorithms.{HEFTScheduler, MinMinScheduler}
import itmo.escience.simenv.simulator.BasicSimulator

//import itmo.escience.simenv.algorithms.gaOld.cga.CoevGAScheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl._
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.utilities.Units._
import itmo.escience.simenv.utilities.Utilities._

/**
  * Created by mikhail on 29.01.2016.
  */
class GADynamExp(wfPath: String, envArray: List[List[Double]], globNet: Double, locNet: Double, reliability: Double,
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
//    println(environment.asInstanceOf[CarrierNodeEnvironment[CapacityBasedNode]].envPrint())

    val scheduler = new GAScheduler(crossoverProb = 0.5,
      mutationProb = 0.3,
      swapMutationProb = 0.3,
      popSize = 50,
      iterationCount = 100)
    //
    val ctx = new BasicContext[DaxTask, CapacityBasedNode](environment, Schedule.emptySchedule[DaxTask, CapacityBasedNode](),
      estimator, 0.0, new SingleAppWorkload(wf))
    //

//    val minmin_schedule = MinMinScheduler.schedule(ctx.asInstanceOf[Context[DaxTask, Node]], environment.asInstanceOf[Environment[Node]])
//    val heft_schedule = HEFTScheduler.schedule(ctx.asInstanceOf[Context[DaxTask, Node]], environment.asInstanceOf[Environment[Node]])
//    val ga_schedule = scheduler.schedule(ctx, environment)
//
//    println("_________")
//    println("GA SCHEDULE:")
//    println(ga_schedule.prettyPrint())
//    println(s"GA makespan: ${ga_schedule.makespan()}")
//    println("_________")
//    println("MINMIN SCHEDULE:")
////    println(minmin_schedule.prettyPrint())
//    println(s"MinMin makespan: ${minmin_schedule.makespan()}")
//    println("_________")
//    println("HEFT SCHEDULE:")
////    println(heft_schedule.prettyPrint())
//    println(s"HEFT makespan: ${heft_schedule.makespan()}")

    val simulator = new BasicSimulator(scheduler, ctx, nodeDownTime, resDownTime)
    simulator.init()
    simulator.runSimulation()

    print("GA Makespan:")
    println(ctx.schedule.makespan())
    ctx.schedule.makespan()


//    println("________")
//    println("COEVOLUTION SCHEDULE")
//    val (coev_schedule, coev_env) = coevScheduler.scheduleAndConfiguration(ctx.asInstanceOf[Context[DaxTask, Node]], environment.asInstanceOf[Environment[Node]])
//    println(coev_schedule.prettyPrint())
//    println("COEVOLUTION ENVIRONMENT")
//    println(coev_env.envPrint())
//    println(s"coev makespan: ${coev_schedule.makespan()}")
//    println(ctx.schedule.prettyPrint())

  }
}
