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
      val vmFrom = node.children(random.nextInt(vmNumber))
      val vmTo = node.children(random.nextInt(vmNumber))
      if (random.nextBoolean() && vmFrom.cores > 1) {
        // change cores
        val coresTrans = random.nextInt(vmFrom.cores - 1) + 1
        source.addCoresValue(vmFrom.id, coresTrans)
        source.addCoresValue(vmTo.id, coresTrans)
      } else {
        if (vmFrom.ram > 1) {
          // change ram
          val ramTrans = random.nextInt(vmFrom.ram - 1) + 1
          source.addRamValue(vmFrom.id, ramTrans)
          source.addRamValue(vmTo.id, ramTrans)
        }
      }

    }
  }

}
