package itmo.escience.simenv.algorithms.ga.quality

import java.util
import java.util.Random

import org.uncommons.watchmaker.framework.{EvaluatedCandidate, SelectionStrategy}

/**
  * Created by user on 17.02.2016.
  */
class QBSelectionStrategy extends SelectionStrategy[QBScheduleSolution]{

  throw new NotImplementedError()

  override def select[S <: QBScheduleSolution](list: util.List[EvaluatedCandidate[S]], b: Boolean, i: Int, random: Random): util.List[S] = ???
}
