package itmo.escience.simenv.experiments

import java.util

import itmo.escience.simenv.algorithms.ga.env.EnvConfigurationProblem
import itmo.escience.simenv.algorithms.{HEFTScheduler, Scheduler}
import itmo.escience.simenv.algorithms.ga._
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl._
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.utilities.Utilities._

/**
  * Created by mikhail on 21.01.2016.
  */
class UrgentCostOptimization extends Experiment {
  var ctx: Context[DaxTask, CapacityBasedNode] = null
  var env: Environment[CapacityBasedNode] = null
  var scheduler: Scheduler = null

  // Params
  val wf_name = "CyberShake_30"
  val deadMultiplier = 2.5
  val deadline = 31.18

  val idealCapacity: Double = 20
  val nodeTypes: List[Double] = List(10, 15, 25, 30)
  val bandwidth = 20 * 1024 * 1024

  val privRel = 0.95
  val pubRel = 0.8

  val requiredRel = 0.99

  // Other
  val costs: List[Double] = nodeTypes.map(x => x * 10)
  val costsMap: java.util.HashMap[Double, Double] = new java.util.HashMap[Double, Double]()
  for (i <- costs.indices) {
    costsMap.put(nodeTypes(i), costs(i))
  }

  val basepath = ".\\resources\\wf-examples\\"
//  val wf1 = parseDAX(basepath + wf_name + ".xml", deadline * deadMultiplier)
  val wf1 = parseDAX(basepath + wf_name + ".xml", 230.0 * deadMultiplier)


  def initSchedule(): (Schedule[DaxTask, CapacityBasedNode], BasicEnvironment) = {
    println("Init schedule")
    println("wf = " + wf_name + "; " + "deadMult = " + deadMultiplier)

    val workload = new MultiWfWorkload(List(wf1))

    var nodes = List[CapacityBasedNode]()

    var fixedNodes = List[CapacityBasedNode]()
    for (i <- nodeTypes.indices) {
      val res: CapacityBasedNode = new CapacityBasedNode(id = s"res_$i", name = s"res_$i", reliability = privRel,
        capacity = nodeTypes(i))
      fixedNodes :+= res
    }

    var publicNodes = List[CapacityBasedNode]()


    nodes = fixedNodes ++ publicNodes
    val globalNet = new Network(id = generateId(), name = "global net", bandwidth = bandwidth, nodes)

    val networks = List(globalNet)

    val initEnv = new BasicEnvironment(fixedNodes, publicNodes, networks, nodeTypes)
    val estimator = new BasicEstimator[CapacityBasedNode](idealCapacity, initEnv, bandwidth)

    ctx = new BasicContext[DaxTask, CapacityBasedNode](initEnv, Schedule.emptySchedule(),
      estimator, 0.0, workload, costsMap, fixRel=privRel, pubRel=pubRel)

    val heftSched = HEFTScheduler.schedule(ctx, initEnv)
    println("HEFT makespan = " + heftSched.makespan())
    val heftSol = WorkflowSchedulingProblem.scheduleToSolution(heftSched, ctx, initEnv)

    val seeds: java.util.ArrayList[WFSchedSolution] = new java.util.ArrayList[WFSchedSolution]()
    seeds.add(heftSol)

    scheduler = new GAFixScheduler(crossoverProb = 0.4,
      mutationProb = 0.2,
      swapMutationProb = 0.3,
      popSize = 50,
      iterationCount = 100, seeds = seeds)

    val ga_res = scheduler.schedule(ctx, initEnv)
    println("!GA SCHEDULE:")
    //          println(ga_res.prettyPrint())
    println(s"GA makespan: ${ga_res.makespan()}")
    println("-----")
    (ga_res, initEnv)

  }

  def init(): Unit = {
  }


  // ++ RUN!!!!!!!
  override def run(): Unit = {

    val maxReplicas = evMaxReplicas(privRel, pubRel)
    val (initSched, initEnv) = initSchedule()
    //Initial schedule and environment

    val firstSol = WorkflowSchedulingProblem.scheduleToSolution(initSched, ctx, initEnv)

    val (initSchedRel, initEnvRel) = scheduleReplication(initSched, initEnv, maxReplicas)
    ctx.setEnvironment(initEnvRel)

    val fitEval = new ScheduleFitnessEvaluator[DaxTask, CapacityBasedNode](ctx, initEnvRel)
    val initCost = fitEval.evaluateNodeCosts(initSchedRel, initEnvRel)
    println(s"Init cost = $initCost")
    val initSol = WorkflowSchedulingProblem.scheduleToSolution(initSchedRel, ctx, initEnvRel)
    val initRel = fitEval.evaluateReliability(initSol, initSchedRel, initEnvRel)
    println(s"Init rel = $initRel")

    val coevRes = coevAlgorithm(initEnvRel, initSchedRel)
    coevRes


    println("Finished")
  }
  // -- RUN

  def evMaxReplicas(priv: Double, pub: Double) = {
    var counter = 0
    var rel = priv
    while (rel < requiredRel) {
      var q = (1 - rel) * pub * 0.99
      counter += 1
      rel += q
    }
    counter
  }

  def scheduleReplication(initSched: Schedule[DaxTask, CapacityBasedNode], initEnv: BasicEnvironment, replicas: Int):
  (Schedule[DaxTask, CapacityBasedNode], BasicEnvironment) = {

    val estimator = new BasicEstimator[CapacityBasedNode](idealCapacity, initEnv, bandwidth)

    val context = new BasicContext[DaxTask, CapacityBasedNode](initEnv, Schedule.emptySchedule(),
      estimator, 0.0, new MultiWfWorkload(List(wf1)), costsMap, privRel, pubRel)

    val initSol = WorkflowSchedulingProblem.scheduleToSolution[DaxTask, CapacityBasedNode](initSched, context, initEnv)



    var nodes = List[CapacityBasedNode]()
    var fixedCopyNodes = List[CapacityBasedNode]()
    var newSchedule = Schedule.emptySchedule[DaxTask, CapacityBasedNode]()
    val fixedNodes = initEnv.fixedNodes

    var publicNodes = List[CapacityBasedNode]()

    for (n <- fixedNodes) {
      val cN = n.copy(n.reliability, true)
      fixedCopyNodes :+= cN
    }

    // replicas
    //      if (nodeSched.nonEmpty) {
    for (r <- 0 until replicas) {
      for (n <- fixedNodes) {
        val nodeSched = initSched.getMap.get(n.id)
        val nodeIdx = fixedCopyNodes.length + publicNodes.length
        val newId = "rep_" + nodeIdx
        val rN = new CapacityBasedNode(id = newId, name = newId, capacity = n.capacity, reliability = pubRel,
          status = n.status, parent = n.parent, fixed = false)
        //        newSchedule.addNode(rN.id)
        for (item <- nodeSched) {
          initSol.addAfter(new MappedTask(item.asInstanceOf[TaskScheduleItem[DaxTask, CapacityBasedNode]].task.id, nodeIdx, 0.99))
          //          newSchedule.getMap.get(rN.id).add(item.asInstanceOf[TaskScheduleItem[DaxTask, CapacityBasedNode]].copy(rN))
        }
        publicNodes :+= rN
      }
    }

    val newEnv = new BasicEnvironment(fixedCopyNodes, publicNodes, List[Network](), nodeTypes)

    val newCtx = new BasicContext[DaxTask, CapacityBasedNode](newEnv, newSchedule,
      estimator, 0.0, new MultiWfWorkload(List(wf1)), costsMap, privRel ,pubRel)
    newSchedule = WorkflowSchedulingProblem.solutionToSchedule[DaxTask, CapacityBasedNode](initSol, newCtx, newEnv)

    val testSol = WorkflowSchedulingProblem.scheduleToSolution[DaxTask, CapacityBasedNode](newSchedule, newCtx, newEnv)
    println("Init:" + initSched.makespan())
    println("Replica:" + newSchedule.makespan())

    (newSchedule, newEnv)
  }

  def coevAlgorithm(env: BasicEnvironment, initSched: Schedule[DaxTask, CapacityBasedNode]) = {

    val initSchedSol = WorkflowSchedulingProblem.scheduleToSolution(initSched, ctx, env)
    val initEnvSol = EnvConfigurationProblem.environmentToSolution(env)
    val seeds = new util.ArrayList[EvSolution[_]]()
    seeds.add(initEnvSol)
    seeds.add(initSchedSol)

    val scheduler = new CGAScheduler(crossoverProb = 0.4,
      mutationProb = 0.3,
      popSize = 50,
      iterationCount = 100, seedPairs = seeds)

    val ga_res = scheduler.asInstanceOf[CGAScheduler].costSchedule(ctx, env)
    val sched = ga_res._1
    val gaCost = ga_res._2
    println(s"GA cost $gaCost")
    println(sched.prettyPrint())
  }


}
