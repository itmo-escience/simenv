package itmo.escience.simenv.algorithms.ga

import org.uma.jmetal.operator.MutationOperator
import org.uma.jmetal.util.JMetalException

/**
 * Created by user on 02.12.2015.
 */
class WorkflowSchedulingMutation(probability:Double, swapProbability: Double) extends MutationOperator[WorkflowSchedulingSolution]{
  override def execute(source: WorkflowSchedulingSolution): WorkflowSchedulingSolution = {
    if (null == source) {
      throw new JMetalException("Null parameter")
    }
    //doMutation(source)
    source
  }

  private def doMutation(source:WorkflowSchedulingSolution) = {
    throw new NotImplementedError()
  }
}
