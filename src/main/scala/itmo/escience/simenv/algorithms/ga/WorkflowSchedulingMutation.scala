package itmo.escience.simenv.algorithms.ga

import itmo.escience.simenv.environment.entities.{Node, CoreRamHddBasedNode, DaxTask, Context}
import itmo.escience.simenv.environment.entitiesimpl.PhysResourceEnvironment
import jdk.nashorn.internal.runtime.regexp.joni.constants.NodeStatus
import org.uma.jmetal.operator.MutationOperator
import org.uma.jmetal.util.JMetalException

import scala.util.Random

/**
 * Created by user on 02.12.2015.
 */
class WorkflowSchedulingMutation(probability:Double, swapProbability: Double, context:Context[DaxTask, CoreRamHddBasedNode]) extends MutationOperator[WorkflowSchedulingSolution]{

  private val random = new Random(System.currentTimeMillis)

  /**
   * The mutation doesn't create new individual but modify existed one
   * @param source
   * @return
   */
  override def execute(source: WorkflowSchedulingSolution): WorkflowSchedulingSolution = {

    if (null == source) {
      throw new JMetalException("Null parameter")
    }

    if (random.nextDouble() <= probability) {
      doMutation(source)
    }

    if (random.nextDouble() <= swapProbability) {
      doSwapMutation(source)
    }

    source
  }

  private def doMutation(source:WorkflowSchedulingSolution) = {

    val liveNodes = context.environment.asInstanceOf[PhysResourceEnvironment].vms.filter(x => x.status == Node.UP).toList
    val node = liveNodes(random.nextInt(liveNodes.length))

    val i = random.nextInt(source.getNumberOfVariables)
    val gene = source.getVariableValue(i)

    source.setVariableValue(i, MappedTask(gene.taskId, node.id))

  }

  private def doSwapMutation(source:WorkflowSchedulingSolution) = {

    val a = random.nextInt(source.getNumberOfVariables)
    val b = random.nextInt(source.getNumberOfVariables)

    val gene_1 = source.getVariableValue(a)
    val gene_2 = source.getVariableValue(b)

    source.setVariableValue(a, MappedTask(gene_1.taskId, gene_2.nodeId))
    source.setVariableValue(b, MappedTask(gene_2.taskId, gene_1.nodeId))
  }

}
