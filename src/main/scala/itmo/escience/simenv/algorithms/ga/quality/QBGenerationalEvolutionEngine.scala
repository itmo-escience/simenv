package itmo.escience.simenv.algorithms.ga.quality

import java.util.Random

import org.uncommons.watchmaker.framework._


class QBGenerationalEvolutionEngine (candidateFactory:QBIndividualsGenerator,
                                     evolutionScheme:QBEvolutionScheme,
                                     fitnessEvaluator:QBFitnessEvaluator,
                                     selectionStrategy:QBSelectionStrategy,
                                     rng: Random)
  extends GenerationalEvolutionEngine[QBScheduleSolution](
      candidateFactory,
      evolutionScheme,
      fitnessEvaluator,
      selectionStrategy,
      rng) {

    throw new NotImplementedError()

}


