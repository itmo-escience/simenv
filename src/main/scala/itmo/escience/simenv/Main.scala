package itmo.escience.simenv

import java.io.PrintWriter

import itmo.escience.simenv.algorithms.ultraGA.{MishanyaWorkflowSchedulingProblem, MishanyaGAScheduler}
import itmo.escience.simenv.algorithms.{MinMinScheduler, HEFTScheduler}
import itmo.escience.simenv.algorithms.ga.{GAScheduler, WorkflowSchedulingProblem}
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.{SingleAppWorkload, BasicContext, OldEstimator, CarrierNodeEnvironment}
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.experiments._
import itmo.escience.simenv.utilities.RandomWFGenerator
import itmo.escience.simenv.utilities.Units._
import itmo.escience.simenv.utilities.Utilities.generateId
import itmo.escience.simenv.utilities.wfGenAlg.WfGeneratorGA

/**
 * Created by Mishanya on 12.10.2015.
 */

/** This is the enter point into the simulator.
  */
object Main {
  def main(args: Array[String]) {

    expMain()

//    val alg = new WfGeneratorGA(crossoverProb=0.5, mutationProb=0.2, popSize=10, iterationCount=100)
//    alg.run(5, 1, 3000)

    println("Finished")
  }

  def expMain() = {
    val envArray = List(List(20.0),List(20.0),List(20.0),List(20.0),List(20.0))
//    val globNet = 1.0
    val globNet = 100.0
    val locNet = 100.0

    val env = genEnvironment(envArray, locNet, globNet)
    val oldEstim = genOldEstimator(env)

    // 0 - genSwan; 1 - genBsm; 2 - swanBsm; 3 - bsmAnal
//    val dataArr = Array[Int](100, 100, 30, 10)
    val dataArr = Array[Int](100, 100, 30, 10, 10)

    val wfGen = RandomWFGenerator.type4GAGenerate(dataArr)
    val wf = RandomWFGenerator.generateWf(wfGen, "bdsm")

    val oldCtx = new BasicContext[DaxTask, CapacityBasedNode](env, Schedule.emptySchedule[DaxTask, CapacityBasedNode](),
      oldEstim, 0.0, new SingleAppWorkload(wf))

    // old HEFT experiment
    val oldHeftSchedule = MinMinScheduler.schedule[DaxTask, CapacityBasedNode](oldCtx, env)
    val oldHeftSolution = WorkflowSchedulingProblem.
          scheduleToSolution[DaxTask, CapacityBasedNode](oldHeftSchedule, oldCtx, env)

    println(s"old HEFT makespan = ${oldHeftSchedule.makespan()}")

    // old GA experiment
    val oldGAScheduler = new GAScheduler(crossoverProb = 0.5, mutationProb = 0.6,
      swapMutationProb = 0.3, popSize = 500, iterationCount = 1000)

    val oldGASolution = oldGAScheduler.run[DaxTask, CapacityBasedNode](oldCtx, env)
    val oldGASchedule = WorkflowSchedulingProblem.
      solutionToSchedule[DaxTask, CapacityBasedNode](oldGASolution, oldCtx, env)

    println(s"old GA makespan = ${oldGASchedule.makespan()}")
    println(s"old GA makespan = ${oldGASolution.fitness}")

    val oldProfit = (1 - (oldGASolution.fitness / oldHeftSchedule.makespan())) * 100


    println(s"HEFT schedule: \n${oldHeftSchedule.prettyPrint()}")
    println()
    println(s"GA schedule: \n${oldGASchedule.prettyPrint()}")

    println(s"old Profit = $oldProfit")





  }

  def genEnvironment(envArray: List[List[Double]], locNet: Double, globNet: Double): Environment[CapacityBasedNode] = {
    var networks = List[Network]()
    var nodes = List[Node]()
    for ((l, i) <- envArray.zipWithIndex) {
      val res: CapacityBasedCarrier = new CapacityBasedCarrier(id = s"res_$i", name = s"res_$i",
        capacity = l.sum, reliability = 1.0)

      for ((l2, j) <- l.zipWithIndex) {
        val node: CapacityBasedNode = new CapacityBasedNode(id = s"res_${i}_node_$j", name = s"res_${i}_node_$j",
          capacity = l2, parent = res.id, reliability = 1.0)
        res.addChild(node)
      }
      val localNet = new Network(id = generateId(), name = "local net", bandwidth = locNet, res.children)
      networks :+= localNet
      nodes :+= res
    }

    val globalNet = new Network(id = generateId(), name = "global net", bandwidth = globNet, nodes)
    networks :+= globalNet

    val environment: Environment[CapacityBasedNode] = new CarrierNodeEnvironment[CapacityBasedNode](nodes, networks)
    environment
  }

  def genOldEstimator(env: Environment[CapacityBasedNode]): OldEstimator[CapacityBasedNode] = {
    val estimator = new OldEstimator[CapacityBasedNode](20, env)
    estimator
  }

}
