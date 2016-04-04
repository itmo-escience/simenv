package itmo.escience.simenv.algorithms.ga

import java.util
import java.util.concurrent._
import java.util.{Collections, Random}

import itmo.escience.simenv.algorithms.ga.env.{MappedEnv, EnvCandidateFactory, EnvConfSolution}
import itmo.escience.simenv.environment.entities.{CapacityBasedNode, Node, Task}
import itmo.escience.simenv.utilities.MathFunctions
import org.uncommons.util.concurrent.ConfigurableThreadFactory
import org.uncommons.util.id.{IDSource, IntSequenceIDSource, StringPrefixIDSource}
import org.uncommons.watchmaker.framework._
import org.uncommons.watchmaker.framework.termination.GenerationCount

import scala.collection.JavaConversions._

/**
  * Created by mikhail on 25.01.2016.
  */
class CoevolutionGenerationalEvolutionEngine[T <: Task, N <: Node](schedFactory: ScheduleCandidateFactory[T, N], envFactory: EnvCandidateFactory[T, N],
                                                                   schedMutOperator: ScheduleMutationOperator[T, N], schedCrossOperator: ScheduleCrossoverOperator, envOperators: EvolutionaryOperator[EnvConfSolution],
                                                                   fitnessEvaluator: ScheduleFitnessEvaluator[T, N],
                                                                   selectionStrategy: SelectionStrategy[Object], rng: Random) extends GenerationalEvolutionEngine[WFSchedSolution](schedFactory,
  schedMutOperator, fitnessEvaluator, selectionStrategy, rng) {

  private var concurrentWorker: BuddiesEvaluationWorker = null
  private val observers: util.Set[EvolutionObserver[_ >: WFSchedSolution]] = new util.HashSet[EvolutionObserver[_ >: WFSchedSolution]]

  var _popSize: Int = _
  var best: (WFSchedSolution, EnvConfSolution, Double) = null
  var gen: Int = 0

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

    schedPop.addAll(selectionStrategy.select(evalSchedPop, fitnessEvaluator.isNatural, _popSize - eliteCount, rng))
    envPop.addAll(selectionStrategy.select(evalEnvPop, fitnessEvaluator.isNatural, _popSize - eliteCount, rng))

    var schedPop1: util.List[WFSchedSolution] = schedPop
    var envPop1: util.List[EnvConfSolution] = envPop
    schedPop1 = schedCrossOperator.apply(schedPop, rng)
    schedPop1 = schedMutOperator.apply(schedPop1, rng)
    envPop1 = envOperators.apply(envPop, rng)

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

    val newSchedPop: util.ArrayList[WFSchedSolution] = new util.ArrayList[WFSchedSolution](schedPop)
    for (buddy <- schedBuddies) {
      if (!newSchedPop.contains(buddy._1)) {
        newSchedPop.add(buddy._1)
      }
    }
    val newEnvPop: util.ArrayList[EnvConfSolution] = new util.ArrayList[EnvConfSolution](envPop)
    for (buddy <- envBuddies) {
      if (!newEnvPop.contains(buddy._1)) {
        newEnvPop.add(buddy._1)
      }
    }


    //     Other
    //    for (s <- schedPop) {
    //      val fit  = fitnessEvaluator.getFitness(s, best._2)
    //      s.fitness = fit
    //      if (best._3 > fit) {
    //        best = (s.copy, best._2, fit)
    //      }
    //    }
    //    for (e <- envPop) {
    //      val fit = fitnessEvaluator.getFitness(best._1, e)
    //      e.fitness = fit
    //        if (best._3 > fit) {
    //          best = (best._1, e, fit)
    //        }
    //    }

    val evalSchedPop = getEvaluatedPopulationS(newSchedPop)
    val evalEnvPop = getEvaluatedPopulationE(newEnvPop)

    //    val evalSchedPop = getEvaluatedPopulationS(schedPop)
    //    val evalEnvPop = getEvaluatedPopulationE(envPop)

    (evalSchedPop, evalEnvPop)
  }

  def evolve(populationSize: Int, eliteCount: Int, seedCandidates: util.Collection[EvSolution[_]], conditions: TerminationCondition): (WFSchedSolution, EnvConfSolution, Double) = {
    evolvePopulation(populationSize, eliteCount, seedCandidates, conditions)
  }

  def evolvePopulation(populationSize: Int, eliteCount: Int, seedCandidates: util.Collection[EvSolution[_]], conditions: TerminationCondition): (WFSchedSolution, EnvConfSolution, Double) = {
    _popSize = populationSize
    if (eliteCount >= 0 && eliteCount < populationSize) {
      if(conditions == null) {
        throw new IllegalArgumentException("At least one TerminationCondition must be specified.")
      } else {
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
        best = (schedPop.head, envPop.head, 66613666)

        var res = evaluatePopulation(schedPop, envPop)
        var evalSchedPop: util.List[EvaluatedCandidate[WFSchedSolution]] = res._1
        var evalEnvPop: util.List[EvaluatedCandidate[EnvConfSolution]] = res._2

        EvolutionUtils.sortEvaluatedPopulation(evalSchedPop, fitnessEvaluator.isNatural)
        EvolutionUtils.sortEvaluatedPopulation(evalEnvPop, fitnessEvaluator.isNatural)

        var schedData = EvolutionUtils.getPopulationData(evalSchedPop, fitnessEvaluator.isNatural, eliteCount, gen, startTime)
        var envData = EvolutionUtils.getPopulationData(evalEnvPop, fitnessEvaluator.isNatural, eliteCount, gen, startTime)


        while (!conditions.shouldTerminate(schedData)) {
          gen += 1
          res = nextEvolutionStep(evalSchedPop, evalEnvPop, eliteCount, rng)
          evalSchedPop = res._1
          evalEnvPop = res._2

          EvolutionUtils.sortEvaluatedPopulation(evalSchedPop, fitnessEvaluator.isNatural)
          EvolutionUtils.sortEvaluatedPopulation(evalEnvPop, fitnessEvaluator.isNatural)

          schedData = EvolutionUtils.getPopulationData(evalSchedPop, fitnessEvaluator.isNatural, eliteCount, gen, startTime)
          envData = EvolutionUtils.getPopulationData(evalEnvPop, fitnessEvaluator.isNatural, eliteCount, gen, startTime)

          //          println(s"gen: ${gen}; best fit: ${best._3}")
          //          println("sched size = " + evalSchedPop.size)
          //          println("env size = " + evalEnvPop.size)
          //          println(best._1.genSeq.map(x => "(" + x.taskId + ": " + x.nodeId + ")"))
          //          println(best._2.genSeq.map(x => "(" + x.vmId + ": " + x.cap + ")"))
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
      val availableNodes = envPop.filter(x => canBeInteracted(s, x))
      //      val curBuddies = scala.util.Random.shuffle(availableNodes.toList).take(math.min(20, availableNodes.size)).
      val curBuddies = scala.util.Random.shuffle(availableNodes.toList).take(math.min(4, availableNodes.size)).
        map(x => (s, x))
      val azaza = scala.util.Random.shuffle(envPop.toList).take(1) :+ best._2.copy
      for (sz <- azaza) {
        val sAdapt = adaptation(s.copy, sz)
        buddies.add((sAdapt, sz))
      }
      buddies.addAll(curBuddies)
      //      buddies.add((s, best._2))
    }
    for (e <- envPop) {
      val availableScheds = schedPop.filter(x => canBeInteracted(x, e))
      //      val curBuddies = scala.util.Random.shuffle(availableNodes.toList).take(math.min(20, availableNodes.size)).
      val curBuddies = scala.util.Random.shuffle(availableScheds.toList).take(math.min(4, availableScheds.size)).
        map(x => (x, e))
      val azaza = scala.util.Random.shuffle(schedPop.toList).take(1) :+ best._1.copy
      for (sz <- azaza) {
        val sAdapt = adaptation(sz.copy, e)
        buddies.add((sAdapt, e))
      }
      buddies.addAll(curBuddies)

    }
    //    println("Buddies size = " + buddies.size)
    buddies
  }

  def canBeInteracted(s: WFSchedSolution, e: EnvConfSolution) : Boolean = {
    s.maxNodeIdx < e.size

//    val sNodes = s.genSeq.map(x => x.nodeId).distinct
//    !e.genSeq.exists(x => sNodes.contains(x.vmId) && x.cap == 0)
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
      case e : Exception =>
        println("ASASASA")
        throw new IllegalStateException("PIZDA")
    }



    //    for (pair <- buddies) {
    ////      val sAdapt = adaptation(pair._1.copy, pair._2.copy)
    //      val fit = fitnessEvaluator.getFitness(pair._1, pair._2)
    //      friendship.put(pair, fit)
    //      if (best == null || fit < best._3) {
    //        best = (pair._1.copy, pair._2.copy, fit)
    ////        pair._1.setGenes(sAdapt)
    //      }
    //    }
    //    println("Average fit = " + (friendship.map(x => x._2).sum / friendship.size))
    for (buddies <- friendship) {
      if (buddies._2 < best._3) {
        best = (buddies._1._1.copy, buddies._1._2.copy, buddies._2)
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
      //      k.fitness = v.sum / v.size
      k.fitness = v.min
    }
  }
  def averageFitnessE(solutions: util.Map[EnvConfSolution, util.List[Double]]) = {
    for ((k, v) <- solutions) {
      //      k.fitness = v.sum / v.size
      k.fitness = v.min
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

  def adaptation(sched: WFSchedSolution, env: EnvConfSolution): WFSchedSolution = {
    var genes: List[MappedTask] = List[MappedTask]()
    val nodeSize = env.size
    for (x <- sched.genSeq) {
      if (x.nodeIdx < nodeSize) {
        genes :+= x
      } else {
        genes :+= new MappedTask(x.taskId, rng.nextInt(nodeSize), x.rel)
      }
    }
    new WFSchedSolution(genes)
  }


//  def adaptation(sched: WFSchedSolution, env: EnvConfSolution): WFSchedSolution = {
//    var genes: List[MappedTask] = List[MappedTask]()
//    val emptyNodes = env.genSeq.filter(x => x.capacity == 0).map(x => x.vmId)
//    val availableNodes = env.genSeq.filter(x => x.capacity > 0).map(x => x.vmId)
//    for (x <- sched.genSeq) {
//      if (availableNodes.contains(x.nodeId)) {
//        genes :+= x
//      } else {
//        genes :+= new MappedTask(x.taskId, availableNodes(rng.nextInt(availableNodes.size)))
//      }
//    }
//    new WFSchedSolution(genes)
//  }

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

