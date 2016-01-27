package itmo.escience.simenv.algorithms.ga

import java.util
import java.util.concurrent._
import java.util.{Collections, Random}

import itmo.escience.simenv.algorithms.ga.env.{EnvCandidateFactory, EnvConfSolution}
import itmo.escience.simenv.environment.entities.{Node, Task}
import org.uncommons.util.concurrent.ConfigurableThreadFactory
import org.uncommons.util.id.{IntSequenceIDSource, IDSource, StringPrefixIDSource}
import org.uncommons.watchmaker.framework._
import scala.collection.JavaConversions._

/**
  * Created by mikhail on 25.01.2016.
  */
class CoevolutionGenerationalEvolutionEngine[T <: Task, N <: Node](schedFactory: ScheduleCandidateFactory[T, N], envFactory: EnvCandidateFactory[T, N],
   schedOperators: EvolutionaryOperator[WFSchedSolution], envOperators: EvolutionaryOperator[EnvConfSolution],
   fitnessEvaluator: ScheduleFitnessEvaluator[T, N],
   selectionStrategy: SelectionStrategy[Object], rng: Random) extends GenerationalEvolutionEngine[WFSchedSolution](schedFactory,
  schedOperators, fitnessEvaluator, selectionStrategy, rng) {

  private var concurrentWorker: BuddiesEvaluationWorker = null
  private val observers: util.Set[EvolutionObserver[_ >: WFSchedSolution]] = new util.HashSet[EvolutionObserver[_ >: WFSchedSolution]]

  var best: (WFSchedSolution, EnvConfSolution, Double) = null

  def nextEvolutionStep(evalSchedPop: util.List[EvaluatedCandidate[WFSchedSolution]], evalEnvPop: util.List[EvaluatedCandidate[EnvConfSolution]], eliteCount: Int, rng: Random):
                                                                            (util.List[EvaluatedCandidate[WFSchedSolution]], util.List[EvaluatedCandidate[EnvConfSolution]]) = {
    val schedPop: util.List[WFSchedSolution]  = new util.ArrayList[WFSchedSolution](evalSchedPop.size())
    val envPop: util.List[EnvConfSolution] = new util.ArrayList[EnvConfSolution](evalEnvPop.size())

    val schedElite: util.List[WFSchedSolution] = new util.ArrayList[WFSchedSolution](eliteCount)
    val envElite: util.List[EnvConfSolution] = new util.ArrayList[EnvConfSolution](eliteCount)

    val schedIterator: util.Iterator[EvaluatedCandidate[WFSchedSolution]] = evalSchedPop.iterator()
    val envIterator: util.Iterator[EvaluatedCandidate[EnvConfSolution]] = evalEnvPop.iterator()

    while(schedElite.size() < eliteCount) {
      schedElite.add(schedIterator.next().getCandidate)
    }
    while(envElite.size() < eliteCount) {
      envElite.add(envIterator.next().getCandidate)
    }

    schedPop.addAll(selectionStrategy.select(evalSchedPop, fitnessEvaluator.isNatural, evalSchedPop.size() - eliteCount, rng))
    envPop.addAll(selectionStrategy.select(evalEnvPop, fitnessEvaluator.isNatural, evalEnvPop.size() - eliteCount, rng))

    val schedPop1 : util.List[WFSchedSolution] = schedOperators.apply(schedPop, rng)
    val envPop1 : util.List[EnvConfSolution] = envOperators.apply(envPop, rng)
    schedPop1.addAll(schedElite)
    envPop1.addAll(envElite)

    evaluatePopulation(schedPop1, envPop1)
  }

  def evaluatePopulation(schedPop: util.List[WFSchedSolution], envPop: util.List[EnvConfSolution]):
          (util.List[EvaluatedCandidate[WFSchedSolution]], util.List[EvaluatedCandidate[EnvConfSolution]]) = {
    val buddies: util.List[(WFSchedSolution, EnvConfSolution)] = createBuddies(schedPop, envPop)
    val friendship: util.Map[(WFSchedSolution, EnvConfSolution), Double] = evaluateFriendship(buddies)
    val (schedBuddies, envBuddies) = parseFriendship(friendship)
    averageFitnessS(schedBuddies)
    averageFitnessE(envBuddies)
    val evalSchedPop = getEvaluatedPopulationS(schedPop)
    val evalEnvPop = getEvaluatedPopulationE(envPop)
    (evalSchedPop, evalEnvPop)
  }

  def evolve(populationSize: Int, eliteCount: Int, conditions: TerminationCondition): (WFSchedSolution, EnvConfSolution, Double) = {
    evolvePopulation(populationSize, eliteCount, Collections.emptySet[EvSolution[_]], conditions)
  }

  def evolvePopulation(populationSize: Int, eliteCount: Int, seedCandidates: util.Collection[EvSolution[_]], conditions: TerminationCondition): (WFSchedSolution, EnvConfSolution, Double) = {
    if (eliteCount >= 0 && eliteCount < populationSize) {
      if(conditions == null) {
        throw new IllegalArgumentException("At least one TerminationCondition must be specified.");
      } else {
        var gen: Int = 0
        val startTime = System.currentTimeMillis()

        val schedSeed: util.List[WFSchedSolution] = seedCandidates.toList.filter(x => x.isInstanceOf[WFSchedSolution]).map(x => x.asInstanceOf[WFSchedSolution])
        val envSeed: util.List[EnvConfSolution] = seedCandidates.toList.filter(x => x.isInstanceOf[EnvConfSolution]).map(x => x.asInstanceOf[EnvConfSolution])

        val schedPop: util.List[WFSchedSolution] = schedFactory.generateInitialPopulation(populationSize, schedSeed, rng)
        val envPop: util.List[EnvConfSolution] = envFactory.generateInitialPopulation(populationSize, envSeed, rng)
        for (x <- schedPop) {
          x.fitness = 66613666
        }
        for (x <- envPop) {
          x.fitness = 66613666
        }


        var res = evaluatePopulation(schedPop, envPop)
        var evalSchedPop: util.List[EvaluatedCandidate[WFSchedSolution]] = res._1
        var evalEnvPop: util.List[EvaluatedCandidate[EnvConfSolution]] = res._2

        EvolutionUtils.sortEvaluatedPopulation(evalSchedPop, fitnessEvaluator.isNatural)
        EvolutionUtils.sortEvaluatedPopulation(evalEnvPop, fitnessEvaluator.isNatural)

        var schedData = EvolutionUtils.getPopulationData(evalSchedPop, fitnessEvaluator.isNatural, eliteCount, gen, startTime)
        var envData = EvolutionUtils.getPopulationData(evalEnvPop, fitnessEvaluator.isNatural, eliteCount, gen, startTime)
        notifyPopulationChange(schedData)

        var satisfiedConditions: util.List[TerminationCondition] = EvolutionUtils.shouldContinue(schedData, conditions)

        while (satisfiedConditions != null) {
          gen += 1
          res = nextEvolutionStep(evalSchedPop, evalEnvPop, eliteCount, rng)
          evalSchedPop = res._1
          evalEnvPop = res._2

          EvolutionUtils.sortEvaluatedPopulation(evalSchedPop, fitnessEvaluator.isNatural)
          EvolutionUtils.sortEvaluatedPopulation(evalEnvPop, fitnessEvaluator.isNatural)

          schedData = EvolutionUtils.getPopulationData(evalSchedPop, fitnessEvaluator.isNatural, eliteCount, gen, startTime)
          envData = EvolutionUtils.getPopulationData(evalEnvPop, fitnessEvaluator.isNatural, eliteCount, gen, startTime)
          notifyPopulationChange(schedData)

          // Increment
          satisfiedConditions = EvolutionUtils.shouldContinue(schedData, conditions)
        }
        best
      }
    } else {
      throw new IllegalArgumentException("Elite count must be non-negative and less than population size.");
    }
  }

  def createBuddies(schedPop: util.List[WFSchedSolution], envPop: util.List[EnvConfSolution]): util.List[(WFSchedSolution, EnvConfSolution)] = {
    val buddies: util.List[(WFSchedSolution, EnvConfSolution)] = new util.ArrayList[(WFSchedSolution, EnvConfSolution)]
    for (s <- schedPop) {
      val availableNodes = envPop.filter(x => s.maxNodeIdx <= x.size)
      if (availableNodes.isEmpty) {
        val newEnv: EnvConfSolution = envFactory.generateRandomCandidate(rng, s.maxNodeIdx)
        envPop.add(newEnv)
        buddies.add((s, newEnv))
      } else {
        buddies.addAll(scala.util.Random.shuffle(availableNodes).take(math.min(4, availableNodes.size)).
          map(x => (s, x)))
      }
    }
    buddies
  }

  def evaluateFriendship(buddies: util.List[(WFSchedSolution, EnvConfSolution)]): util.Map[(WFSchedSolution, EnvConfSolution), Double] = {
    val friendship: util.Map[(WFSchedSolution, EnvConfSolution), Double] = new util.HashMap[(WFSchedSolution, EnvConfSolution), Double]()


    try {
      val ex1: util.List[(WFSchedSolution, EnvConfSolution)] = Collections.unmodifiableList(buddies)
      val results1: util.ArrayList[Future[(WFSchedSolution, EnvConfSolution, Double)]] = new util.ArrayList[Future[(WFSchedSolution, EnvConfSolution, Double)]](buddies.size())
      val i: util.Iterator[(WFSchedSolution, EnvConfSolution)] = buddies.iterator()

      while(i.hasNext) {
        val result: (WFSchedSolution, EnvConfSolution) = i.next
        results1.add(getSharedWorker.submit(new BuddiesEvaluationTask(fitnessEvaluator, result)))
      }

      val i2 = results1.iterator()

      while(i2.hasNext) {
        val result1: Future[(WFSchedSolution, EnvConfSolution, Double)] = i2.next
        val res: (WFSchedSolution, EnvConfSolution, Double) = result1.get
        friendship.put((res._1, res._2), res._3)
      }
    } catch {
      case e : Exception => throw new IllegalStateException("PIZDA")
    }



    for (pair <- buddies) {
      val fit = fitnessEvaluator.getFitness(pair._1, pair._2)
      friendship.put(pair, fit)
      if (best == null || fit < best._3) {
        best = (pair._1, pair._2, fit)
      }
    }
    friendship
  }

  def parseFriendship(friendship: util.Map[(WFSchedSolution, EnvConfSolution), Double]):
  (util.Map[WFSchedSolution, util.List[Double]], util.Map[EnvConfSolution, util.List[Double]]) = {
    val schedBuddies: util.Map[WFSchedSolution, util.List[Double]] = new util.HashMap[WFSchedSolution, util.List[Double]]()
    val envBuddies: util.Map[EnvConfSolution, util.List[Double]] = new util.HashMap[EnvConfSolution, util.List[Double]]()
    for ((k, v) <- friendship) {
      val sched = k._1
      val env = k._2
      if (!schedBuddies.containsKey(sched)) {
        schedBuddies.put(sched, new util.ArrayList[Double]())
      }
      if (!envBuddies.containsKey(env)) {
        envBuddies.put(env, new util.ArrayList[Double]())
      }
      schedBuddies.get(sched).add(v)
      envBuddies.get(env).add(v)
    }
    (schedBuddies, envBuddies)
  }

  def averageFitnessS(solutions: util.Map[WFSchedSolution, util.List[Double]]) = {
    for ((k, v) <- solutions) {
      k.fitness = v.sum / v.size
    }
  }
  def averageFitnessE(solutions: util.Map[EnvConfSolution, util.List[Double]]) = {
    for ((k, v) <- solutions) {
      k.fitness = v.sum / v.size
    }
  }

  def getEvaluatedPopulationS(pop: util.List[WFSchedSolution]): util.List[EvaluatedCandidate[WFSchedSolution]] = {
    pop.map(x => new EvaluatedCandidate[WFSchedSolution](x, x.fitness))
  }
  def getEvaluatedPopulationE(pop: util.List[EnvConfSolution]): util.List[EvaluatedCandidate[EnvConfSolution]] = {
    pop.map(x => new EvaluatedCandidate[EnvConfSolution](x, x.fitness))
  }

  def getSharedWorker: BuddiesEvaluationWorker = {
    if (concurrentWorker == null) {
      concurrentWorker = new BuddiesEvaluationWorker()
    }
    concurrentWorker
  }



  override def addEvolutionObserver(observer: EvolutionObserver[_ >: WFSchedSolution]): Unit = {
    observers.add(observer)
  }

  override def removeEvolutionObserver(observer: EvolutionObserver[_ >: WFSchedSolution]): Unit = {
    observers.remove(observer)
  }

  def notifyPopulationChange(data: PopulationData[WFSchedSolution]) {
    val i: util.Iterator[EvolutionObserver[_ >: WFSchedSolution]] = observers.iterator()

    while(i.hasNext) {
      val observer: EvolutionObserver[WFSchedSolution] = i.next.asInstanceOf[EvolutionObserver[WFSchedSolution]]
      observer.populationUpdate(data)
    }
  }

}

class BuddiesEvaluationTask(fitnessEvaluator: ScheduleFitnessEvaluator[_ <: Task, _ <: Node], pair: (WFSchedSolution, EnvConfSolution)) extends Callable[(WFSchedSolution, EnvConfSolution, Double)] {
  override def call(): (WFSchedSolution, EnvConfSolution, Double) = {
    (pair._1, pair._2, fitnessEvaluator.getFitness(pair._1, pair._2))
  }
}

class BuddiesEvaluationWorker {
  val  WORKER_ID_SOURCE: IDSource[String] = new StringPrefixIDSource("FitnessEvaluationWorker", new IntSequenceIDSource())
  val workQueue: LinkedBlockingQueue[Runnable] = new LinkedBlockingQueue()
  val threadFactory: ConfigurableThreadFactory = new ConfigurableThreadFactory(WORKER_ID_SOURCE.nextID(), 5, true)
  val executor: ThreadPoolExecutor = new ThreadPoolExecutor(Runtime.getRuntime.availableProcessors(), Runtime.getRuntime.availableProcessors(), 60L, TimeUnit.SECONDS, this.workQueue, threadFactory);
  executor.prestartAllCoreThreads()

  def submit(task: BuddiesEvaluationTask): Future[(WFSchedSolution, EnvConfSolution, Double)] = {
    executor.submit(task)
  }

  def main(args: Array[String]) = {
    new BuddiesEvaluationWorker()
  }

  override def finalize() = {
    executor.shutdown()
    super.finalize()
  }
}

