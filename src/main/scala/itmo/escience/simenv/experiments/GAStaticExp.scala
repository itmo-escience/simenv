package itmo.escience.simenv.experiments

import itmo.escience.simenv.algorithms.ga.GAScheduler
import itmo.escience.simenv.algorithms.{HEFTScheduler, MinMinScheduler}

import scala.util.Random

//import itmo.escience.simenv.algorithms.gaOld.cga.CoevGAScheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl._
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.utilities.Utilities._
import itmo.escience.simenv.utilities.Units._

/**
  * Created by mikhail on 29.01.2016.
  */
class GAStaticExp(wfPath: String, envArray: List[List[Double]], globNet: Double, locNet: Double, reliability: Double) extends Experiment{
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



  override def run() = {
//    val rnd = new Random()
//    val pop = 10 + rnd.nextInt(500)
//    println("pop " + pop)
//    val cx = rnd.nextDouble()
//    val m1 = rnd.nextDouble()
//    val m2 = rnd.nextDouble()
//    println(s"$cx   $m1    $m2")
    val scheduler = new GAScheduler(crossoverProb = 0.85,
      mutationProb = 0.15,
      swapMutationProb = 0.15,
      popSize = 150,
      iterationCount = 1000)
//    val scheduler = new GAScheduler(crossoverProb = cx,
//      mutationProb = m1,
//      swapMutationProb = m2,
//      popSize = 100,
//      iterationCount = 1000)

    val ctx = new BasicContext[DaxTask, CapacityBasedNode](environment, Schedule.emptySchedule[DaxTask, CapacityBasedNode](),
      estimator, 0.0, new SingleAppWorkload(wf))

//    val minmin_schedule = MinMinScheduler.schedule(ctx.asInstanceOf[Context[DaxTask, Node]], environment.asInstanceOf[Environment[Node]])
//    val hstartTime = System.currentTimeMillis()
//    val heft_schedule = HEFTScheduler.schedule(ctx.asInstanceOf[Context[DaxTask, Node]], environment.asInstanceOf[Environment[Node]])
//    val htime = System.currentTimeMillis() - hstartTime
//    println(s"Heft makespan: ${heft_schedule.makespan()}")
//    println("HEFT Time = " + htime)

    var stat = 0.0
    var values = List[Double]()
    for (i <- 0 until 10)
    {
//      val startTime = System.currentTimeMillis()
      val ga_schedule = scheduler.schedule(ctx, environment)
//      val time = System.currentTimeMillis() - startTime
//      println(s"GA makespan: ${ga_schedule.makespan()}")
      stat += ga_schedule.makespan()
      values :+= ga_schedule.makespan()
    }
    stat = stat / 10
    println(values)
    println(s"GA: $stat\n")
//    println("Time = " + time)
    stat
  }
}
