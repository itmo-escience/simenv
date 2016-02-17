package itmo.escience.simenv.algorithms.ga.quality

import java.util
import java.util.Random

import org.uncommons.watchmaker.framework.CandidateFactory

/**
  * Created by user on 17.02.2016.
  */
class QBIndividualsGenerator extends CandidateFactory[QBScheduleSolution]{

  throw new NotImplementedError()
  override def generateInitialPopulation(i: Int, random: Random): util.List[QBScheduleSolution] = ???

  override def generateInitialPopulation(i: Int, collection: util.Collection[QBScheduleSolution], random: Random): util.List[QBScheduleSolution] = ???

  override def generateRandomCandidate(random: Random): QBScheduleSolution = ???
}
