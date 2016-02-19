package itmo.escience.simenv.ga

import java.util
import java.util.Random

import itmo.escience.simenv.entities._
import org.uncommons.watchmaker.framework.EvolutionaryOperator
import scala.collection.JavaConversions._


/**
  * Created by mikhail on 22.01.2016.
  */
class ScheduleMutationOperator(env: CarrierNodeEnvironment[CpuRamNode], tasks: util.HashMap[String, DaxTask],
                                                     probability: Double)
                                                      extends EvolutionaryOperator[SSSolution]{

  override def apply(mutants: util.List[SSSolution], random: Random): util.List[SSSolution] = {
    val mutatedPopulation: util.ArrayList[SSSolution] = new util.ArrayList[SSSolution](mutants.size())
    val it: util.Iterator[SSSolution] = mutants.iterator()

    while(it.hasNext) {
      val s: SSSolution = it.next()
      mutatedPopulation.add(mutateSolution(s, random))
    }
    mutatedPopulation
  }

  def mutateSolution(mutant: SSSolution, rnd: Random): SSSolution = {
    if (rnd.nextDouble() <= probability) {
      doMutation(mutant, rnd)
    }
    mutant
  }

  private def doMutation(mutant:SSSolution, rnd: Random) = {
    val taskIds = tasks.keySet().toList
    val mutT = taskIds(rnd.nextInt(taskIds.size))
    val curN = mutant.getVal(mutT)
    // change node
    changeNode(mutant, mutT, rnd)
//      case 13 => transferProc(mutant, mutT, nodeTasksList, rnd)
//      case 69 => changeProc(mutant, mutT, proc, rnd)
//      case 21 => increaseProc(mutant, mutT, proc, rnd)

  }

  private def changeNode(mutant: SSSolution, task: String, rnd: Random) = {
    val item = mutant.getVal(task)
    val nodeIds = env.nodesIds
    val newNode = nodeIds(rnd.nextInt(nodeIds.size))
    mutant.put(task, newNode)
//    val repaired = StormSchedulingProblem.repairMap(mutant.genes)
//    mutant.setGenes(repaired)
  }

//  private def transferProc(mutant: SSSolution, task: String, nodeTasks: List[String], rnd: Random) = {
//    val otherTasks = nodeTasks.filter(x => x!= task)
//    val other = otherTasks(rnd.nextInt(otherTasks.size))
//    val item = mutant.getVal(task)
//    val otherItem = mutant.getVal(other)
//    val transProc = rnd.nextDouble() * item._2
//    mutant.put(task, (item._1, item._2 - transProc))
//    mutant.put(other, (item._1, otherItem._2 + transProc))
//  }

//  private def changeProc(mutant: SSSolution, task: String, proc: Double, rnd: Random) = {
//    val item = mutant.getVal(task)
//    val freeProc = 1.0 - proc
//    mutant.put(task, (item._1, rnd.nextDouble() * (item._2 + freeProc)))
//  }
//  private def increaseProc(mutant: SSSolution, task: String, proc: Double, rnd: Random) = {
//    val item = mutant.getVal(task)
//    val freeProc = 1.0 - proc
//    mutant.put(task, (item._1, item._2 + rnd.nextDouble() * freeProc))
//  }
}
