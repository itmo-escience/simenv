//package itmo.escience.simenv.algorithms.vm.env
//
//import java.util
//
//import itmo.escience.simenv.algorithms.RandomScheduler
//import itmo.escience.simenv.algorithms.wm.WorkflowSchedulingSolution
//import itmo.escience.simenv.environment.entities._
//import itmo.escience.simenv.utilities.Utilities._
//import org.uma.jmetal.problem.Problem
//
//import scala.collection.JavaConversions._
//import scala.util.Random
//
///**
// * Created by user on 02.12.2015.
// */
//
//object EnvConfigurationProblem {
//
//  def environmentToSolution(env: CarrierNodeEnvironment[CpuTimeNode]):EnvConfigurationSolution = {
//    val genes = env.nodes.map(x => MappedVm(x.id, x.cpuTime)).toList
//    new EnvConfigurationSolution(genes)
//  }
//
//  def solutionToEnvironment(solution: EnvConfigurationSolution, context: Context[DaxTask, CpuTimeNode]): CarrierNodeEnvironment[CpuTimeNode] = {
//    // reconstruction of environment in context with new configuration of vms
//    val carriers = context.environment.carriers.map(x => x.asInstanceOf[CpuTimeCarrier])
//    val vms = context.environment.asInstanceOf[CarrierNodeEnvironment[CpuTimeNode]].nodes
//    var newNodes: List[CpuTimeCarrier] = List()
//    for (n <- carriers) {
//      val res: CpuTimeCarrier = new CpuTimeCarrier(id=n.id, name=n.name,
//        cores=n.cores, cpuTime=n.cpuTime,
//        reliability=n.reliability
//      )
//      for (vm <- vms.filter(x => x.parent == res.id)) {
//        val vmItem = solution.getVmElement(vm.id)
//        res.addChild(new CpuTimeNode(id=vm.id, name=vm.name, cores=vm.cores, cpuTime=vmItem.cpuTime, parent=res.id, reliability=vm.reliability))
//      }
//      newNodes :+= res
//    }
//
//    //TODO DANGER!!!!!!!!!!!
//    val Mb_sec_100 = 1024*1024*100/8
//    val networks = context.environment.networks // List(new Network(id=generateId(), name="", bandwidth=Mb_sec_100, newNodes))
//    val environment: CarrierNodeEnvironment[CpuTimeNode] = new CarrierNodeEnvironment[CpuTimeNode](newNodes, networks)
//
////    val newSchedule = context.schedule.fixedSchedule()
////
////    val schedSolution = WorkflowSchedulingProblem.scheduleToSolution(context.schedule, context)
////
////     repair sequence in relation with parent-child dependencies
////     construct new schedule by placing it in task-by-task manner
////    val repairedOrdering = WorkflowSchedulingProblem.repairOrdering(schedSolution, context)
////
////    for (x <- schedSolution) {
////      val (task, nodeId) = x
////      newSchedule.placeTask(task,
////        environment.asInstanceOf[PhysResourceEnvironment].vmById(nodeId).asInstanceOf[CoreRamHddBasedNode],
////        context)
////    }
////    newSchedule
//    environment
//  }
//
////  private class Pair[T](val num: Int, val value: T) extends Comparable[Pair[T]]{
////    override def compareTo(o: Pair[T]): Int = num.compareTo(o.num)
////  }
//}
//
//class EnvConfigurationProblem(wf:Workflow, newSchedule:Schedule, nodes:Seq[CpuTimeNode], context:Context[DaxTask, CpuTimeNode]) extends Problem[EnvConfigurationSolution]{
//
//  override def getNumberOfObjectives: Int = 1
//
//  override def getNumberOfConstraints: Int = 0
//
//  override def getName: String = "EnvironmentConfiguration"
//
//  override def evaluate(s: EnvConfigurationSolution): Unit = {
//    val env = EnvConfigurationProblem.solutionToEnvironment(s, context)
//
//    //++
//    val resSchedule = newSchedule.fixedSchedule()
//
//    val schedSolution: WorkflowSchedulingSolution = WorkflowSchedulingProblem.scheduleToSolution(newSchedule, context.asInstanceOf[Context[DaxTask, Node]])
//
////    repair sequence in relation with parent-child dependencies
////      construct new schedule by placing it in task-by-task manner
//    val repairedOrdering = WorkflowSchedulingProblem.repairOrdering(schedSolution, context.asInstanceOf[Context[DaxTask, Node]])
//
//    for (x <- repairedOrdering) {
//      val (task, nodeId) = x
//      resSchedule.placeTask(task,
//        env.nodeById(nodeId),
//        context.asInstanceOf[Context[DaxTask, Node]])
//    }
//    //--
//    val makespan = resSchedule.makespan()
//    s.setObjective(0, makespan)
//  }
//
//  override def getNumberOfVariables: Int = wf.tasks.length
//
//  override def createSolution(): EnvConfigurationSolution = {
//    val rnd: Random = new Random()
//
//    val carriers = context.environment.carriers
//    val vms = context.environment.asInstanceOf[CarrierNodeEnvironment[CpuTimeNode]].nodes
//    var genes: List[MappedVm] = List()
//    for (n <- carriers) {
//      val vmIds = vms.filter(x => x.parent == n.id).map(x => x.id)
//      val vmNumber: Int = vmIds.size
//      var allocatedCpu: Double = 0
//
//      for (i <- 0 until vmNumber - 1) {
//        val vmId = vmIds.get(i)
//        val vmCpu = rnd.nextDouble() * (100 - allocatedCpu)
//        allocatedCpu += vmCpu
//        genes :+= new MappedVm(vmId, vmCpu)
//      }
//      genes :+= new MappedVm(vmIds.last, 100 - allocatedCpu)
//    }
//    new EnvConfigurationSolution(genes)
//  }
//}
//
