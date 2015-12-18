package itmo.escience.simenv.experiments

import java.util

import itmo.escience.simenv.algorithms.ga.GAScheduler
import itmo.escience.simenv.environment.entities.{CapacityBasedNode, DaxTask, Network, Schedule}
import itmo.escience.simenv.environment.entitiesimpl.{BasicContext, BasicEnvironment, BasicEstimator, SingleAppWorkload}
import itmo.escience.simenv.utilities.ScheduleHelper
import itmo.escience.simenv.utilities.Utilities._

import scala.beans.BeanProperty
import scala.collection.JavaConversions._


object ExternalGARun {

  def run(config: SimpleConfig) = {

    //    val yaml = new Yaml(new org.yaml.snakeyaml.Loader(new Constructor(classOf[SimpleConfig])))
    //    val config = yaml.load(ymlStr).asInstanceOf[SimpleConfig]

    val ctx = makeContext(config)

    val schedule = new GAScheduler(crossoverProb = config.crossoverProbability,
      mutationProb = config.mutationProbability,
      swapMutationProb = config.swapMutationProbability,
      popSize = config.populationSize,
      iterationCount = config.iterationCount).schedule(ctx)

    ctx.schedule = schedule
    ScheduleHelper.checkStaticSchedule(ctx)
    val makespan = schedule.makespan()

    makespan
  }

  private def makeContext(config: SimpleConfig) = {

    val nodes = config.nodes.map(x => new CapacityBasedNode(id=generateId(), name="", nominalCapacity=x))
    val wf = parseDAX(config.wfPath)

    val Mb_sec_100 = 1024*1024*100/8

    val networks = List(new Network(id=generateId(), name="", bandwidth=Mb_sec_100, nodes))
    val environment = new BasicEnvironment(nodes, networks)
    val estimator = new BasicEstimator(idealCapacity = 1.0, environment)

    val ctx = new BasicContext[DaxTask, CapacityBasedNode](environment, Schedule.emptySchedule(),
      estimator, 0.0, new SingleAppWorkload(wf))

    ctx
  }

  /**
   * With the Snakeyaml Constructor approach shown in the main method,
   * this class must have a no-args constructor.
   */
  class SimpleConfig {
    @BeanProperty var wfPath = ""
    @BeanProperty var crossoverProbability =  0.4
    @BeanProperty var mutationProbability = 0.3
    @BeanProperty var swapMutationProbability = 0.2
    @BeanProperty var populationSize = 50
    @BeanProperty var iterationCount = 50
    @BeanProperty var nodes = new java.util.ArrayList[Double]()
  }

}




