package itmo.escience.simenv.algorithms.ga.vmga

import itmo.escience.simenv.environment.entities._
import org.uma.jmetal.operator.MutationOperator
import org.uma.jmetal.util.JMetalException

import scala.util.Random

/**
 * Created by user on 02.12.2015.
 */
class EnvConfigurationMutation(probability:Double, context:Context[DaxTask, CpuTimeNode]) extends MutationOperator[EnvConfigurationSolution]{

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

    val nodes = context.environment.carriers.filter(x => x.children.size > 1)
    if (nodes.nonEmpty) {
      val node = nodes(random.nextInt(nodes.length))
      val vmNumber = node.children.size
      val nodeChildren = source.vmsSeq().filter(x => node.children.map(y => y.id).contains(x.vmId))
      val vmFrom = nodeChildren(random.nextInt(vmNumber))
      if (vmFrom > 1 && )
      val vmTo = nodeChildren(random.nextInt(vmNumber))
      // change cpuTime
      val cpuTrans = random.nextDouble() * (vmFrom.cpuTime - 1)
      source.addValue(vmFrom.vmId, -cpuTrans)
      source.addValue(vmTo.vmId, cpuTrans)

    }
  }

}
