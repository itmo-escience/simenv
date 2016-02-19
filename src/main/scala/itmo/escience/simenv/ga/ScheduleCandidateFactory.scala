package itmo.escience.simenv.ga

import java.util
import java.util.Random

import itmo.escience.simenv.entities._
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory
import scala.collection.JavaConversions._

/**
  * Created by mikhail on 22.01.2016.
  */
class ScheduleCandidateFactory(env: CarrierNodeEnvironment[CpuRamNode], tasks: util.HashMap[String, DaxTask]) extends AbstractCandidateFactory[SSSolution]{

  val nodeIds: List[String] = env.nodesIds

  override def generateRandomCandidate(random: Random): SSSolution = {

    val map: java.util.HashMap[String, String] = new java.util.HashMap[String, String]

    for (t <- tasks.keySet()) {
      map.put(t, nodeIds(random.nextInt(nodeIds.size)))
    }

//    val repMap = StormSchedulingProblem.repairMap(map)
    new SSSolution(map)
  }
}
