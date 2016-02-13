package itmo.escience.simenv.experiments

import itmo.escience.simenv.algorithms.vm.env.EnvConfigurationProblem
import itmo.escience.simenv.algorithms.{RandomScheduler, Scheduler}
import itmo.escience.simenv.algorithms.ga.{WorkflowSchedulingProblem, CGAScheduler, GAScheduler}
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl._
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.utilities.JSONParser
import itmo.escience.simenv.utilities.Utilities._
import itmo.escience.simenv.utilities.Units._

/**
  * Created by mikhail on 21.01.2016.
  */
class CGACloudCostOptimization extends Experiment {
  var ctx: Context[DaxTask, CapacityBasedNode] = null
  var env: Environment[CapacityBasedNode] = null
  var scheduler: Scheduler = null

  val wfs = List[String]("Montage_25", "CyberShake_30", "Epigenomics_24", "Inspiral_30", "Sipht_60")
  val deadMultipliers = List[Double](2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0)
  val deadlines = List[Double](31.18, 2051.34, 2945.15, 674.09, 2232.96)
  val deadMap = new java.util.HashMap[String, Double]()
  wfs.zip(deadlines).foreach(x => deadMap.put(x._1, x._2))

  val idealCapacity: Double = 20
  val nodeTypes: List[Double] = List(0.075, 0.15, 0.3, 0.6, 1.2, 2.5, 5, 10, 20, 40)
  val costs: List[Double] = nodeTypes.map(x => x * 10)
  val costsMap: java.util.HashMap[Double, Double] = new java.util.HashMap[Double, Double]()
  for (i <- costs.indices) {
    costsMap.put(nodeTypes(i), costs(i))
  }

  val basepath = ".\\resources\\wf-examples\\"

  def init(): Unit = {
    // dynamic parameters
    val wf_name = wfs(1)
    val deadline = deadMap.get(wf_name)
    val deadMultiplier = deadMultipliers(0)



    val wf1 = parseDAX(basepath + wf_name + ".xml", deadline * deadMultiplier)
    val workload = new MultiWfWorkload(List(wf1))

    var nodes = List[CapacityBasedNode]()
    for (i <- 0 until 5) {
      val res: CapacityBasedNode = new CapacityBasedNode(id=s"res_$i", name=s"res_$i",
        capacity = nodeTypes(i))
      nodes :+= res
    }

    val bandwidth = 20 Mb_Sec
    val globalNet = new Network(id=generateId(), name="global net", bandwidth=bandwidth, nodes)

    val networks = List(globalNet)

    env = new BasicEnvironment(nodes, networks, nodeTypes)
    val estimator = new BasicEstimator[CapacityBasedNode](idealCapacity, env, bandwidth)

    scheduler = new CGAScheduler(crossoverProb=0.4,
      mutationProb=0.2,
      swapMutationProb=0.3,
      popSize=50,
      iterationCount=100)

    ctx = new BasicContext[DaxTask, CapacityBasedNode](env, Schedule.emptySchedule(),
      estimator, 0.0, workload, costsMap)


//    val solutionsPath = "d:\\Projects\\simenv_dead\\resources\\solutions\\"
    val solutionsPath = ".\\resources\\solutions\\"
    var deadStr = "hooy"
    if (deadMultiplier.toInt.toDouble != deadMultiplier) {
      deadStr = deadMultiplier + ""
    } else {
      deadStr = deadMultiplier.toInt + ""
    }
    val icpcpFile = solutionsPath + deadStr + "_" + "IC-PCP_" + wf_name + ".json"
//    val lddlsFile = solutionsPath + deadStr + "_" + "LDD-LS_" + wf_name + ".json"
    val icpcpSol = JSONParser.parseSolution(icpcpFile)
    val icpcpEnv = EnvConfigurationProblem.solutionToEnvironment[DaxTask, CapacityBasedNode](icpcpSol._2, ctx)
    val icpcpSched = WorkflowSchedulingProblem.solutionToSchedule[DaxTask, CapacityBasedNode](icpcpSol._1, ctx, icpcpEnv)
//    val lddlsSol = JSONParser.parseSolution(lddlsFile)
//    val lddlsEnv = EnvConfigurationProblem.solutionToEnvironment[DaxTask, CapacityBasedNode](lddlsSol._2, ctx)
//    val lddlsSched = WorkflowSchedulingProblem.solutionToSchedule[DaxTask, CapacityBasedNode](lddlsSol._1, ctx, lddlsEnv)

    val icpcpRes = scheduler.asInstanceOf[CGAScheduler].evaluateSolution[DaxTask, CapacityBasedNode](ctx, env, icpcpSol._1, icpcpSol._2)
//    val lddlsRes = scheduler.asInstanceOf[CGAScheduler].evaluateSolution[DaxTask, CapacityBasedNode](ctx, env, lddlsSol._1, lddlsSol._2)
    println("ICPCP env:" + icpcpSol._2.genSeq.toString())
//    println("LDDLS env:" + lddlsSol._2.genSeq.toString())
    println("ICPCP res: " + icpcpRes)
//    println("LDDLS res: " + lddlsRes)
    println("ICPCP makespan: " + icpcpSched.makespan())
//    println("LDDLS makespan: " + lddlsSched.makespan())
    println("Test")
  }

  override def run(): Unit = {

    val random_schedule = RandomScheduler.schedule[DaxTask, CapacityBasedNode](ctx, env)
    println(s"Random makespan: ${random_schedule.makespan()}")



    val ga_schedule = scheduler.schedule(ctx, env)
    println("GA SCHEDULE:")
//    println(ga_schedule.prettyPrint())
    println(s"GA makespan: ${ga_schedule.makespan()}")
    println("Finished")
  }

}
