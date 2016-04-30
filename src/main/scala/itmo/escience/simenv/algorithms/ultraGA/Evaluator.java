package itmo.escience.simenv.algorithms.ultraGA;

import itmo.escience.simenv.environment.entities.CapacityBasedNode;
import itmo.escience.simenv.environment.entities.DaxTask;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by mikhail on 30.04.2016.
 */
public class Evaluator {
    public static List<EvaluatedCandidate<MishanyaSolution>> evaluatePopulation(List<MishanyaSolution> population, MishanyaScheduleFitnessEvaluator<DaxTask, CapacityBasedNode> fitnessEvaluator, FitnessEvaluationWorker concurrentWorker) {
        ArrayList evaluatedPopulation = new ArrayList(population.size());
        try {
            List ex1 = Collections.unmodifiableList(population);
            ArrayList results1 = new ArrayList(population.size());
            Iterator i$ = population.iterator();

            while (i$.hasNext()) {
                Object result = i$.next();
                results1.add(getSharedWorker(concurrentWorker).submit(new FitnessTask(fitnessEvaluator, result, ex1)));
            }

            i$ = results1.iterator();

            while (i$.hasNext()) {
                Future result1 = (Future) i$.next();
                evaluatedPopulation.add(result1.get());
            }
        } catch (ExecutionException var7) {
            throw new IllegalStateException("Fitness evaluation task execution failed.", var7);
        } catch (InterruptedException var8) {
            Thread.currentThread().interrupt();
        }
        return evaluatedPopulation;
    }


    public static synchronized FitnessEvaluationWorker getSharedWorker(FitnessEvaluationWorker concurrentWorker) {

        if(concurrentWorker == null) {
            concurrentWorker = new FitnessEvaluationWorker();
        }

        return concurrentWorker;
    }
}
