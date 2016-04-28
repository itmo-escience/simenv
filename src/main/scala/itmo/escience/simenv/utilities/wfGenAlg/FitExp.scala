package itmo.escience.simenv.utilities.wfGenAlg

import java.util

import itmo.escience.simenv.algorithms.HEFTScheduler
import itmo.escience.simenv.algorithms.ga.GAScheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl._
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.utilities.RandomWFGenerator
import itmo.escience.simenv.utilities.Utilities._

/**
  * Created by mikhail on 26.04.2016.
  */
class FitExp(wfGen: util.HashMap[String, (Double, List[(String, Double)])], wfName: String, envArray: List[List[Double]], globNet:Double, locNet: Double) {
  val _wfString = wfGen.toString
  val wf = RandomWFGenerator.generateWf(wfGen, wfName)

  var networks = List[Network]()
  var nodes = List[Node]()
  for ((l, i) <- envArray.zipWithIndex) {
    val res: CapacityBasedCarrier = new CapacityBasedCarrier(id=s"res_$i", name=s"res_$i",
      capacity=l.sum, reliability = 1.0)

    for ((l2, j) <- l.zipWithIndex) {
      val node: CapacityBasedNode = new CapacityBasedNode(id = s"res_${i}_node_$j", name = s"res_${i}_node_$j",
        capacity = l2, parent = res.id, reliability = 1.0)
      res.addChild(node)
    }
    val localNet = new Network(id=generateId(), name="local net", bandwidth=locNet, res.children)
    networks :+= localNet
    nodes :+= res
  }

  val globalNet = new Network(id=generateId(), name="global net", bandwidth=globNet, nodes)
  networks :+= globalNet

  val environment: Environment[CapacityBasedNode] = new CarrierNodeEnvironment[CapacityBasedNode](nodes, networks)
  val estimator = new OldEstimator[CapacityBasedNode](20, environment)

  def run(): Double = {

    val scheduler = new GAScheduler(crossoverProb = 0.85,
      mutationProb = 0.15,
      swapMutationProb = 0.15,
      popSize = 50,
      iterationCount = 300)

    val ctx = new BasicContext[DaxTask, CapacityBasedNode](environment, Schedule.emptySchedule[DaxTask, CapacityBasedNode](),
      estimator, 0.0, new SingleAppWorkload(wf))

    val heft_schedule = HEFTScheduler.schedule(ctx, environment)
    val heft_makespan = heft_schedule.makespan()

    var avgMakespan = 0.0
    var values = List[Double]()
    val repeats = 5
    for (i <- 0 until repeats)
    {
      val ga_schedule = scheduler.schedule(ctx, environment)
      val ga_makespan = ga_schedule.makespan()
      avgMakespan += ga_makespan
      values :+= ga_makespan
    }

    def profit(res: Double) = {
      (1 - (res / heft_makespan)) * 100
    }

    avgMakespan = avgMakespan / repeats
    val avgProfit = profit(avgMakespan)
    val profits = values.map(x => profit(x))

//    println("--exp--")
//    println(_wfString)
//    println(s"Heft : $heft_makespan")
//    println(s"GA makespans: $values")
//    println(s"GA   profits: $profits")
//    println(s"GA result: $avgProfit")
    avgProfit
  }
}
