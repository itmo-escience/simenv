package itmo.escience.simenv.algorithms.ga.quality

import org.uncommons.watchmaker.framework.{PopulationData, TerminationCondition}

/**
  * Created by user on 17.02.2016.
  */

object LimitedGenerations {
  def apply(count:Int) = new LimitedGenerations(count)
}

class LimitedGenerations(val count:Int) extends TerminationCondition {

  override def shouldTerminate(populationData: PopulationData[_]): Boolean = {
    populationData.getGenerationNumber > count
  }
}
