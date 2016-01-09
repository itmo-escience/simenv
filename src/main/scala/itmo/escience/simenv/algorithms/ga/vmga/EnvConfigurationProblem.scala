package itmo.escience.simenv.algorithms.ga.vmga

import java.util

import itmo.escience.simenv.algorithms.RandomScheduler
import itmo.escience.simenv.algorithms.ga.{WorkflowSchedulingSolution, WorkflowSchedulingProblem}
import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.{BasicContext, PhysResourceEnvironment, SingleAppWorkload}
import itmo.escience.simenv.utilities.Utilities._
import org.uma.jmetal.problem.Problem

import scala.collection.JavaConversions._
import scala.util.Random

/**
 * Created by user on 02.12.2015.
 */

object EnvConfigurationProblem {

  def environmentToSolution(env: PhysResourceEnvironment):EnvConfigurationSolution = {
    val genes = env.vms.map(x => MappedVm(x.id, (x.cores, x.ram))).toList
    new EnvConfigurationSolution(genes)
  }

  def solutionToEnvironment(solution: EnvConfigurationSolution, context: Context[DaxTask, CoreRamHddBasedNode]): PhysResourceEnvironment = {
    // reconstruction of environment in context with new configuration of vms
    val nodes = context.environment.nodes
    val vms = context.environment.asInstanceOf[PhysResourceEnvironment].vms
    var newNodes: List[CoreRamHddBasedNode] = List()
    for (n <- nodes) {
      val res: PhysicalResource = new PhysicalResource(id=n.id, name=n.name,
        cores=n.cores, ram=n.ram,
        storage=new SimpleStorage(id=n.storage.id, name=n.storage.name, volume=n.storage.asInstanceOf[SimpleStorage].volume),
        reliability=n.reliability
      )
      for (vm <- vms.filter(x => x.parent == res.id)) {
        val vmItem = solution.getVmElement(vm.id)
        res.runVM(vmCores=vmItem.config._1, vmRam=vmItem.config._2, vmStorage=vm.storage.asInstanceOf[SimpleStorage].volume, vmId=vm.id)
      }
      newNodes :+= res
    }

    val Mb_sec_100 = 1024*1024*100/8
    val networks = List(new Network(id=generateId(), name="", bandwidth=Mb_sec_100, newNodes))
    val environment: PhysResourceEnvironment = new PhysResourceEnvironment(newNodes, networks)

//    val newSchedule = context.schedule.fixedSchedule()
//
//    val schedSolution = WorkflowSchedulingProblem.scheduleToSolution(context.schedule, context)
//
//     repair sequence in relation with parent-child dependencies
//     construct new schedule by placing it in task-by-task manner
//    val repairedOrdering = WorkflowSchedulingProblem.repairOrdering(schedSolution, context)
//
//    for (x <- schedSolution) {
//      val (task, nodeId) = x
//      newSchedule.placeTask(task,
//        environment.asInstanceOf[PhysResourceEnvironment].vmById(nodeId).asInstanceOf[CoreRamHddBasedNode],
//        context)
//    }
//    newSchedule
    environment
  }

//  private class Pair[T](val num: Int, val value: T) extends Comparable[Pair[T]]{
//    override def compareTo(o: Pair[T]): Int = num.compareTo(o.num)
//  }
}

class EnvConfigurationProblem(wf:Workflow, newSchedule:Schedule, nodes:Seq[CoreRamHddBasedNode], context:Context[DaxTask, CoreRamHddBasedNode]) extends Problem[EnvConfigurationSolution]{

  override def getNumberOfObjectives: Int = 1

  override def getNumberOfConstraints: Int = 0

  override def getName: String = "EnvironmentConfiguration"

  override def evaluate(s: EnvConfigurationSolution): Unit = {
    val env = EnvConfigurationProblem.solutionToEnvironment(s, context)

    //++
    val resSchedule = newSchedule.fixedSchedule()

    val schedSolution: WorkflowSchedulingSolution = WorkflowSchedulingProblem.scheduleToSolution(newSchedule, context)

//    repair sequence in relation with parent-child dependencies
//      construct new schedule by placing it in task-by-task manner
    val repairedOrdering = WorkflowSchedulingProblem.repairOrdering(schedSolution, context)

    for (x <- repairedOrdering) {
      val (task, nodeId) = x
      resSchedule.placeTask(task,
        env.asInstanceOf[PhysResourceEnvironment].vmById(nodeId).asInstanceOf[CoreRamHddBasedNode],
        context)
    }
    //--
    val makespan = resSchedule.makespan()
    s.setObjective(0, makespan)
  }

  override def getNumberOfVariables: Int = wf.tasks.length

  override def createSolution(): EnvConfigurationSolution = {
    val rnd: Random = new Random()

    val nodes = context.environment.nodes.map(x => x.asInstanceOf[PhysicalResource])
    val vms = context.environment.asInstanceOf[PhysResourceEnvironment].vms
    var genes: List[MappedVm] = List()
    for (n <- nodes) {
      val vmIds = vms.filter(x => x.parent == n.id).map(x => x.id)
      val vmNumber: Int = vmIds.size
      var allocatedCores: Int = 0
      var allocatedRam: Int = 0

      for (i <- 0 until vmNumber - 1) {
        val vmId = vmIds.get(i)
        val vmCores = rnd.nextInt((n.cores - allocatedCores) - (vmNumber - i) + 1) + 1
        val vmRam = rnd.nextInt((n.ram - allocatedRam) - (vmNumber - i) + 1) + 1
        allocatedCores += vmCores
        allocatedRam += vmRam
        genes :+= new MappedVm(vmId, (vmCores, vmRam))
      }
      genes :+= new MappedVm(vmIds.last, (n.cores - allocatedCores, n.ram - allocatedRam))
    }
    new EnvConfigurationSolution(genes)
  }
}

