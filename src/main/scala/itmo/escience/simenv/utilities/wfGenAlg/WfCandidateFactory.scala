package itmo.escience.simenv.utilities.wfGenAlg

import java.util.Random

import itmo.escience.simenv.algorithms.RandomScheduler
import itmo.escience.simenv.environment.entities.{Context, Node, Task}
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory

/**
  * Created by mikhail on 22.01.2016.
  */
class WfCandidateFactory(size: Int, left: Int, right: Int) extends AbstractCandidateFactory[ArrSolution]{

  override def generateRandomCandidate(random: Random): ArrSolution = {
    val arr = new Array[Int](size)
    for (i <- arr.indices) {
      arr(i) = left + random.nextInt(right - left)
    }
    new ArrSolution(arr)
  }
}
