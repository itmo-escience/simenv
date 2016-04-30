package itmo.escience.simenv.algorithms.ultraGA;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.uncommons.util.concurrent.ConfigurableThreadFactory;
import org.uncommons.util.id.IDSource;
import org.uncommons.util.id.IntSequenceIDSource;
import org.uncommons.util.id.StringPrefixIDSource;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;

public class FitnessEvaluationWorker {
    private static final IDSource<String> WORKER_ID_SOURCE = new StringPrefixIDSource("FitnessEvaluationWorker", new IntSequenceIDSource());
    private final LinkedBlockingQueue<Runnable> workQueue;
    private final ThreadPoolExecutor executor;

    public FitnessEvaluationWorker() {
        this(true);
    }

    public FitnessEvaluationWorker(boolean daemonWorkerThreads) {
        this.workQueue = new LinkedBlockingQueue();
        ConfigurableThreadFactory threadFactory = new ConfigurableThreadFactory((String)WORKER_ID_SOURCE.nextID(), 5, daemonWorkerThreads);
        this.executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), 60L, TimeUnit.SECONDS, this.workQueue, threadFactory);
        this.executor.prestartAllCoreThreads();
    }

    public <T> Future<EvaluatedCandidate<T>> submit(FitnessTask<T> task) {
        return this.executor.submit(task);
    }

    public static void main(String[] args) {
        new FitnessEvaluationWorker(false);
    }

    protected void finalize() throws Throwable {
        this.executor.shutdown();
        super.finalize();
    }
}
