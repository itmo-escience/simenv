package itmo.escience.simenv.environment.entitiesimpl

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.modelling.Workload

import scala.collection.mutable

/**
 * Created by Nikolay on 11/29/2015.
 */
class MultiWfWorkload[T <: Task](val wfs: List[Workflow[T]]) extends Workload[T]{

  override def apps: Seq[Workflow[T]] = List(new Workflow[T](id="headWF", name="headWF", deadline=0.0, headTask=head))
  val _headtask = new HeadDaxTask(id ="000", name ="headtask", children=List(), workflowId = "workload")
  for (wf <- wfs) {
    _headtask.children  ++= wf.headTask.asInstanceOf[DaxTask].children
  }

  def head: HeadDaxTask = _headtask

  def deadlines: Map[WorkflowId, Double] = {
    wfs.foldLeft(Map[WorkflowId, Double]())((s, x) => s + (x.id -> x.deadline) )
  }
}
