package itmo.escience.simenv.algorithms.ga

import java.util

import itmo.escience.simenv.algorithms.ga.WorkflowSchedulingSolution
import org.uma.jmetal.operator.CrossoverOperator
import org.uma.jmetal.util.JMetalException

/**
 * Created by user on 02.12.2015.
 */
class WorkflowSchedulingCrossover(probability: Double) extends CrossoverOperator[WorkflowSchedulingSolution]{
  override def execute(source: util.List[WorkflowSchedulingSolution]): util.List[WorkflowSchedulingSolution] = {
    if (null == source) {
      throw new JMetalException("Null parameter") ;
    } else if (source.size() != 2) {
      throw new JMetalException("There must be two parents instead of " + source.size()) ;
    }

    doCrossover(source.get(0), source.get(1)) ;
  }

  private def doCrossover(p1: WorkflowSchedulingSolution, p2: WorkflowSchedulingSolution) ={
    throw new NotImplementedError()
  }

}
