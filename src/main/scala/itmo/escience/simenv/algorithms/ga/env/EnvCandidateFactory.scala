package itmo.escience.simenv.algorithms.ga.env

import java.util.Random

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory

/**
  * Created by mikhail on 26.01.2016.
  */
class EnvCandidateFactory[T <: Task, N <: Node](env: Environment[N]) extends AbstractCandidateFactory[EnvConfSolution] {

  override def generateRandomCandidate(random: Random): EnvConfSolution = {
    var result: List[MappedEnv] = List[MappedEnv]()
    val carriers = env.carriers
    for (carrier <- carriers) {

      val interceptors = scala.util.Random.shuffle(carrier.children.asInstanceOf[List[CapacityBasedNode]].filter(x => x.status == NodeStatus.UP))
      val totalCap: Double = interceptors.foldLeft(0.0)((s, x) => s + x.capacity)
      var curCap: Double = totalCap
      val it: Iterator[CapacityBasedNode] = interceptors.iterator
      while (it.hasNext) {
        val node = it.next()
        if (!it.hasNext) {
         result :+= new MappedEnv(node.id, curCap)
        } else {
          if (curCap > 0) {
            val nodeCap = random.nextInt(curCap.toInt)
            curCap -= nodeCap
            result :+= new MappedEnv(node.id, nodeCap.toDouble)
          } else {
            result :+= new MappedEnv(node.id, 0.0)
          }
        }
      }
    }
    new EnvConfSolution(result)
  }

}
