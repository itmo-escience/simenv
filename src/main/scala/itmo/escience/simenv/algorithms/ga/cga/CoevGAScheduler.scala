//package itmo.escience.simenv.algorithms.ga.cga
//
//import itmo.escience.simenv.algorithms.Scheduler
//import itmo.escience.simenv.algorithms.ga.{WorkflowSchedulingSolution, GAScheduler}
//import itmo.escience.simenv.algorithms.ga.vmga.{EnvConfigurationSolution, GAEnvConfigurator}
//import itmo.escience.simenv.environment.entities._
//import itmo.escience.simenv.environment.modelling.Environment
//
///**
//  * Created by Mishanya on 07.01.2016.
//  */
//class CoevGAScheduler(crossoverProb:Double, mutationProb: Double, swapMutationProb: Double,
//                               popSize:Int, iterationCount: Int,
//                               vmCrossoverProb: Double, vmMutationProb: Double,
//                               vmPopSize:Int, vmIterationCount: Int, coevCycles: Int)
//                                  extends Scheduler[DaxTask, CoreRamNode]{
//
//  def scheduleAndConfiguration(context: Context[DaxTask, Node], environment: Environment[Node]):
//                                (Schedule, CarrierNodeEnvironment[CpuTimeNode]) = {
//    val scheduler = new GAScheduler[Node](crossoverProb = crossoverProb,
//      mutationProb = mutationProb,
//      swapMutationProb = swapMutationProb,
//      popSize = popSize,
//      iterationCount = iterationCount)
//    val configurator = new GAEnvConfigurator(crossoverProb = vmCrossoverProb,
//      mutationProb = vmMutationProb,
//      popSize = vmPopSize,
//      iterationCount = vmIterationCount)
//
//    var schedRes: (Schedule, List[WorkflowSchedulingSolution]) = scheduler.coevSchedule(context, environment, null)
//    var curSched: Schedule = schedRes._1
//    var schedPop: List[WorkflowSchedulingSolution] = schedRes._2
//
//    var confRes: (CarrierNodeEnvironment[CpuTimeNode], List[EnvConfigurationSolution])
//        = configurator.coevEnvironmentConfig(context.asInstanceOf[Context[DaxTask, CpuTimeNode]], curSched, null)
//    var curEnvironment: CarrierNodeEnvironment[CpuTimeNode] = confRes._1
//    var confPop: List[EnvConfigurationSolution] = confRes._2
//
//    for (i <- 0 until coevCycles) {
//      schedRes = scheduler.coevSchedule(context, curEnvironment.asInstanceOf[Environment[Node]], schedPop)
//      confRes = configurator.coevEnvironmentConfig(context.asInstanceOf[Context[DaxTask, CpuTimeNode]], curSched, confPop)
//      curSched = schedRes._1
//      curEnvironment = confRes._1
//      schedPop = schedRes._2
//      confPop = confRes._2
////      if (newSched.makespan() < curSched.makespan()) {
////        curSched = newSched
////      }
//    }
//    (curSched, curEnvironment)
//  }
//
//  override def schedule(context: Context[DaxTask, CoreRamNode], environment: Environment[CoreRamNode]): Schedule = ???
//}
