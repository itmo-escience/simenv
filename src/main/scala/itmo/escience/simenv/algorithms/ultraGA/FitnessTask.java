package itmo.escience.simenv.algorithms.ultraGA;//

import java.util.List;
import java.util.concurrent.Callable;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

class FitnessTask<T> implements Callable<EvaluatedCandidate<T>> {
    private final FitnessEvaluator<? super T> fitnessEvaluator;
    private final T candidate;
    private final List<T> population;

    FitnessTask(FitnessEvaluator<? super T> fitnessEvaluator, T candidate, List<T> population) {
        this.fitnessEvaluator = fitnessEvaluator;
        this.candidate = candidate;
        this.population = population;
    }

    public EvaluatedCandidate<T> call() {
        return new EvaluatedCandidate(this.candidate, this.fitnessEvaluator.getFitness(this.candidate, this.population));
    }
}
