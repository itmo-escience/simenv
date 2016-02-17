package itmo.escience.simenv.algorithms.ga.quality

import java.util.Random

import itmo.escience.simenv.algorithms.ga.WorkflowSchedulingProblem
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.{SingleAppWorkload, BasicContext, BasicEstimator, CarrierNodeEnvironment}
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.utilities.Utilities._
import itmo.escience.simenv.utilities.Units._

/**
  * Created by user on 17.02.2016.
  */
object TempMain {

  val populationSize = 50
  val eliteCount = 10

  val context:Context[DaxTask, CapacityBasedNode] = buildContext()

  def main(args: Array[String]):Unit = {

    val engine = new QBGenerationalEvolutionEngine(new QBIndividualsGenerator(),
      new QBEvolutionScheme(new QBCrossover, new QBMutation),
      new QBFitnessEvaluator(),
      new QBSelectionStrategy(),
      new Random())

    val bestSol = engine.evolve(populationSize, eliteCount, LimitedGenerations(100))

    val schedule = QBFitnessEvaluator.solutionToSchedule(bestSol, context, context.environment)
    println(s"Schedule makespan is ${schedule.makespan()}")
  }

  def buildContext() = {

    val baseNodePower = 20
    val nodesPower = List(10, 15, 25, 30)
    val network_bandwidth = 100 Mbit_Sec
    val wf = buildWorkflow()

    val nodes = for ((power, i) <- nodesPower.zipWithIndex)
      yield new CapacityBasedNode(
        id = s"res_${i}_node_$power",
        name = s"res_${i}_node_$power",
        capacity = power,
        parent = NullNode.id,
        reliability = 1.0)

    val globalNet = new Network(id=generateId(), name="global net", bandwidth=network_bandwidth, nodes)
    val networks = List[Network](globalNet)

    val environment: Environment[CapacityBasedNode] = new CarrierNodeEnvironment[CapacityBasedNode](nodes, networks)
    val estimator = new BasicEstimator[CapacityBasedNode](baseNodePower, environment)

    val ctx = new BasicContext[DaxTask, CapacityBasedNode](
      environment,
      Schedule.emptySchedule[DaxTask, CapacityBasedNode](),
      estimator,
      0.0,
      new SingleAppWorkload(wf))

    ctx
  }

  def buildWorkflow(): Workflow = {
    throw new NotImplementedError()
    new QBWorkflow(
      id = "QBWorkflow",
      name = "QBWorkflow",
      headTask = null,
      tasksAlternative = null)
  }

}
