package itmo.escience.simenv.algorithms.wm

import java.util

import org.uma.jmetal.operator.CrossoverOperator
import org.uma.jmetal.util.JMetalException

import scala.util.Random

/**
 * Created by user on 02.12.2015.
 */
class WorkflowSchedulingCrossover(probability: Double) extends CrossoverOperator[WorkflowSchedulingSolution]{

  private val random = new Random(System.currentTimeMillis)

  /**
   * Crossover creates new individuals and don't modify existed parents
   * @param source list of parents
   * @return list of children
   */
  override def execute(source: util.List[WorkflowSchedulingSolution]): util.List[WorkflowSchedulingSolution] = {

    if (null == source) {
      throw new JMetalException("Null parameter")
    } else if (source.size() != 2) {
      throw new JMetalException("There must be two parents instead of " + source.size())
    }

    if (random.nextDouble() <= probability) {

      val p1 = source.get(0).copy()
      val p2 = source.get(1).copy()

      doCrossover(p1, p2)
    }
    else source
  }

  private def doCrossover(p1: WorkflowSchedulingSolution, p2: WorkflowSchedulingSolution) ={

    // 1. generate 2 points x < y in the first chromosome to perform crossover
    // 2. select all genes (ordered task-node pairs) in the first chomosome between these 2 points
    // 3. find genes with the same tasks' ids in the second chromosome
    // 4. swap nodes between these 2 sets of genes

    // see:
    //Yu, J., & Buyya, R. (2006). Scheduling scientific workflow applications
    // with deadline and budget constraints using genetic algorithms.
    // Scientific Programming, 14(3-4), 217-230.


//    throw new NotImplementedError()
    val size = p1.getNumberOfVariables()
    val (left, right) = leftAndRight(size)

    val genesToBeReplaced_1 = (left until right).map(i => (p1.getVariableValue(i).taskId, i)).toMap
    val genesToBeReplaced_2 = (0 until p2.getNumberOfVariables).map(i => (p2.getVariableValue(i).taskId, i)).
      filter({case (taskId, i)=> genesToBeReplaced_1.contains(taskId)}).toMap

    for ((taskId, i) <- genesToBeReplaced_1) {
      val j = genesToBeReplaced_2(taskId)
      val node_1 = p1.getVariableValue(i).nodeId
      val node_2 = p2.getVariableValue(j).nodeId
      p1.setVariableValue(i, MappedTask(taskId, node_2))
      p2.setVariableValue(j, MappedTask(taskId, node_1))
    }

    val ret = new util.ArrayList[WorkflowSchedulingSolution](2)
    ret.add(p1)
    ret.add(p2)

    ret
  }

  private def leftAndRight(size: Int) = {
    val a = random.nextInt(size)
    val b = random.nextInt(size)
    val left = Math.min(a, b)
    val right = Math.max(a,b)
    (left, right)
  }

}
