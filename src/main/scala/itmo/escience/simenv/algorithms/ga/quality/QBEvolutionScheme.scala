package itmo.escience.simenv.algorithms.ga.quality

import org.uncommons.watchmaker.framework.EvolutionaryOperator
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline
import scala.collection.JavaConversions._

/**
  * Created by user on 17.02.2016.
  */
class QBEvolutionScheme(pipeline:EvolutionaryOperator[QBScheduleSolution]*)
  extends EvolutionPipeline[QBScheduleSolution](pipeline){
  throw new NotImplementedError()
}
