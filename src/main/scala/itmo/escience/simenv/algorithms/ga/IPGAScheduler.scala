package itmo.escience.simenv.algorithms.ga

import java.util

import ifmo.escience.dapris.common.base.ISchedule
import ifmo.escience.dapris.common.base.algorithm.{BaseScheduleAlgorithm}
import ifmo.escience.dapris.common.entities.{Environment, Workload, AlgorithmParameter}
import itmo.escience.simenv.algorithms.Scheduler
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.{IPEstimator, IPEnvironment, SingleAppWorkload, BasicContext}
import scala.collection.JavaConversions._
import itmo.escience.simenv.IPAdapter._

/**
  * Created by mikhail on 13.04.2016.
  */
class IPGAScheduler(env: Environment, schedule: ISchedule,
                    params: util.List[AlgorithmParameter], wl: Workload) extends BaseScheduleAlgorithm(env, schedule, params, wl) {
  var _alg: Scheduler = null
  setParameters(params)

  var _ctx: BasicContext[DaxTask, DetailedNode] = null
  var _wl: SingleAppWorkload = null
  var _env: IPEnvironment = null

  override def run(): Unit = {
    throw new NotImplementedError()
  }

  override def getParameters: util.List[AlgorithmParameter] = super.getParameters

  override def setParameters(newParameters: util.List[AlgorithmParameter]): Unit = {
   parameters = newParameters
    var cx = 0.0
    var mut = 0.0
    var mutS = 0.0
    var popSize = 0
    var iter = 0
    for (p <- newParameters) {
      p.getName match {
        case "mutation" => mut = p.getCurrentValue
        case "swapMutation" => mutS = p.getCurrentValue
        case "crossover" => cx = p.getCurrentValue
        case "populationSize" => popSize = p.getCurrentValue.toInt
        case "iterationsCount" => iter = p.getCurrentValue.toInt
      }
    }
    _alg = new GAScheduler(cx, mut, mutS, popSize, iter)
  }

  override def makespan(): Double = {
    _env = envAdapter(env)
    _wl = wlAdapter(wl)
    val currentTime = 0.0
    val estimator = new IPEstimator(_env)
    _ctx = new BasicContext[DaxTask, DetailedNode](_env, Schedule.emptySchedule[DaxTask, DetailedNode](), estimator,
    currentTime, _wl)
    val resultSchedule = _alg.schedule[DaxTask, DetailedNode](_ctx, _env)

    resultSchedule.makespan()
  }
}
