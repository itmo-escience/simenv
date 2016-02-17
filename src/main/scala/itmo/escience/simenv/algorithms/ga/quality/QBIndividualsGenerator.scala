package itmo.escience.simenv.algorithms.ga.quality

import java.util
import java.util.Random

import itmo.escience.simenv.environment.entities.{Node, Task, Context}
import org.uncommons.watchmaker.framework.CandidateFactory

/**
  * Created by user on 17.02.2016.
  */
class QBIndividualsGenerator[T <: Task, N <: Node](mutPercent:Double, workflow:QBWorkflow, context: Context[T,N]) extends CandidateFactory[QBScheduleSolution]{

  override def generateInitialPopulation(i: Int, random: Random): util.List[QBScheduleSolution] = ???

  override def generateInitialPopulation(i: Int, collection: util.Collection[QBScheduleSolution], random: Random): util.List[QBScheduleSolution] = ???

  override def generateRandomCandidate(random: Random): QBScheduleSolution = {
    throw new NotImplementedError()
//    val tasks = for (task <- workflow.tasks if random.nextDouble() < mutPercent) {
//      val alternateTasks = workflow.tasksAlternative.get(task.id)
//      val i = random.nextInt(alternateTasks.size)
//      yield alternateTasks.take(i)
//    }
//
//    val alterWorkflow = workflow.buildAlternateWorkflow(tasks)
//    // 1. create schedule
//    // 2. convert schedule to solution
//    throw new NotImplementedError()
  }
}
