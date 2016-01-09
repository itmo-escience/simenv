package itmo.escience.simenv.algorithms.ga.vmga

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.PhysResourceEnvironment
import org.uma.jmetal.operator.MutationOperator
import org.uma.jmetal.util.JMetalException

import scala.util.Random

/**
 * Created by user on 02.12.2015.
 */
class EnvConfigurationMutation(probability:Double, context:Context[DaxTask, CoreRamHddBasedNode]) extends MutationOperator[EnvConfigurationSolution]{

  private val random = new Random(System.currentTimeMillis)

  /**
   * The mutation doesn't create new individual but modify existed one
   * @param source
   * @return
   */
  override def execute(source: EnvConfigurationSolution): EnvConfigurationSolution = {

    if (null == source) {
      throw new JMetalException("Null parameter")
    }

    if (random.nextDouble() <= probability) {
      doMutation(source)
    }

    source
  }

  private def doMutation(source:EnvConfigurationSolution) = {

    val nodes = context.environment.nodes.filter(x => x.asInstanceOf[PhysicalResource].children.size > 1)
    if (nodes.nonEmpty) {
      val node = nodes(random.nextInt(nodes.length)).asInstanceOf[PhysicalResource]
      val vmNumber = node.children.size
      val nodeChildren = source.vmsSeq().filter(x => node.children.map(y => y.id).contains(x.vmId))
      val vmFrom = nodeChildren(random.nextInt(vmNumber))
      val vmTo = nodeChildren(random.nextInt(vmNumber))
      if (random.nextBoolean() && vmFrom.config._1 > 1) {
        // change cores
        val coresTrans = random.nextInt(vmFrom.config._1 - 1) + 1
        source.addCoresValue(vmFrom.vmId, -coresTrans)
        source.addCoresValue(vmTo.vmId, coresTrans)
      } else {
        if (vmFrom.config._2 > 1) {
          // change ram
          val ramTrans = random.nextInt(vmFrom.config._2 - 1) + 1
          source.addRamValue(vmFrom.vmId, -ramTrans)
          source.addRamValue(vmTo.vmId, ramTrans)
        }
      }

    }
  }

}
