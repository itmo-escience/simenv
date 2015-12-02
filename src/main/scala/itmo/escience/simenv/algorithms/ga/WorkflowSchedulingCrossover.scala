package itmo.escience.simenv.algorithms.ga

import java.util

import org.uma.jmetal.operator.CrossoverOperator
import org.uma.jmetal.util.JMetalException

import scala.util.Random

/**
 * Created by user on 02.12.2015.
 */
class WorkflowSchedulingCrossover(probability: Double) extends CrossoverOperator[WorkflowSchedulingSolution]{

  val random = new Random(System.currentTimeMillis)

  override def execute(source: util.List[WorkflowSchedulingSolution]): util.List[WorkflowSchedulingSolution] = {
    if (null == source) {
      throw new JMetalException("Null parameter")
    } else if (source.size() != 2) {
      throw new JMetalException("There must be two parents instead of " + source.size())
    }

    //doCrossover(source.get(0), source.get(1)) ;
    source
  }

  private def doCrossover(p1: WorkflowSchedulingSolution, p2: WorkflowSchedulingSolution) ={

    throw new NotImplementedError()
    val size = p1.getNumberOfVariables()
    // generate 2 points to perform crossover
    // (crossover is implemented according to Buyya)
    //
    var a = random.nextInt(size)
    var b = random.nextInt(size)
    a = Math.min(a, b)
    b = Math.max(a,b)

    val ord1 =  p1.tasksSeq()
    val ord2 = p2.tasksSeq()


  }

}
