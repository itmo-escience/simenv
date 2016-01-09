package itmo.escience.simenv.algorithms.ga.cga

import itmo.escience.simenv.algorithms.Scheduler
import itmo.escience.simenv.algorithms.ga.GAScheduler
import itmo.escience.simenv.algorithms.ga.vmga.GAEnvConfigurator
import itmo.escience.simenv.environment.entities.{Schedule, Context, CoreRamHddBasedNode, DaxTask}
import itmo.escience.simenv.environment.entitiesimpl.PhysResourceEnvironment
import itmo.escience.simenv.environment.modelling.Environment

/**
  * Created by Mishanya on 07.01.2016.
  */
class CoevGAScheduler(crossoverProb:Double, mutationProb: Double, swapMutationProb: Double,
                               popSize:Int, iterationCount: Int,
                              vmCrossoverProb: Double, vmMutationProb: Double,
                              vmPopSize:Int, vmIterationCount: Int, coevCycles: Int) extends Scheduler[DaxTask, CoreRamHddBasedNode]{

  def scheduleAndConfiguration(context: Context[DaxTask, CoreRamHddBasedNode], environment: Environment[CoreRamHddBasedNode]): (Schedule, PhysResourceEnvironment) = {
    val scheduler = new GAScheduler(crossoverProb = crossoverProb,
      mutationProb = mutationProb,
      swapMutationProb = swapMutationProb,
      popSize = popSize,
      iterationCount = iterationCount)
    val configurator = new GAEnvConfigurator(crossoverProb = vmCrossoverProb,
      mutationProb = vmMutationProb,
      popSize = vmPopSize,
      iterationCount = vmIterationCount)

    var curSched: Schedule = scheduler.schedule(context, environment)
    var curEnvironment: PhysResourceEnvironment = configurator.environmentConfig(context, curSched)
    for (i <- 0 until coevCycles) {
      curSched = scheduler.schedule(context, curEnvironment)
      curEnvironment = configurator.environmentConfig(context, curSched)
    }
    (curSched, curEnvironment)
  }

  override def schedule(context: Context[DaxTask, CoreRamHddBasedNode], environment: Environment[CoreRamHddBasedNode]): Schedule = ???
}
