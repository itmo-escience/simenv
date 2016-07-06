package itmo.escience.simenv.algorithms.ecgProcessing

import java.util.Random

import itmo.escience.simenv.environment.ecgProcessing.CoreStorageNode
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.CarrierNodeEnvironment
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory

/**
  * Created by mikhail on 26.01.2016.
  */
class EcgEnvCandidateFactory(env: CarrierNodeEnvironment[CoreStorageNode]) extends AbstractCandidateFactory[EcgEnvConfSolution] {

  override def generateRandomCandidate(random: Random): EcgEnvConfSolution = {
    var result: List[EcgMappedEnv] = List[EcgMappedEnv]()
    val carriers = env.carriers
    for (carrier <- carriers) {

      val interceptors = scala.util.Random.shuffle(carrier.children.filter(x => x.status == NodeStatus.UP))
      val totalCap: Int = interceptors.foldLeft(0)((s, x) => s + x.cores)
      var curCap: Int = totalCap
      val it: Iterator[CoreStorageNode] = interceptors.iterator
      while (it.hasNext) {
        val node = it.next()
        if (!it.hasNext) {
         result :+= new EcgMappedEnv(node.id, curCap)
        } else {
          if (curCap > 0) {
            val nodeCap = random.nextInt(curCap)
            curCap -= nodeCap
            result :+= new EcgMappedEnv(node.id, nodeCap)
          } else {
            result :+= new EcgMappedEnv(node.id, 0)
          }
        }
      }
    }
    new EcgEnvConfSolution(result)
  }

}
