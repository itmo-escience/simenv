package itmo.escience.simenv.environment.entities

/**
 * Created by Nikolay on 11/29/2015.
 */
class DaxTask(val id: TaskId, val name: String, val execTime: Double,
                   val inputData: List[DataFile] = List(),
                   val outputData: List[DataFile] = List(),
                   var parents: List[DaxTask] = List(),
                   var children: List[DaxTask],
                   val workflowId: WorkflowId) extends Task {
  override def status: TaskStatus = TaskStatus.UNSTARTED

  override def toString: String = {
    s"DaxTask id: ${id}"
  }

  def volumeToTransfer(parent: DaxTask): Double = {
    if (!parents.contains(parent)) {
      throw new IllegalArgumentException(s"the task ${parent.id} is not a parent for ${id}")
    }

    // TODO: ATTENTION! Situation with ids should be clearified
    val commonFilesID = this.inputData.map(file => file.id).intersect(parent.outputData.map(file => file.id)).toSet
    val files = this.inputData.filter(x => commonFilesID.contains(x.id))
    val transferVolume = files.foldLeft(0.0)((acc: Double, x: DataFile) => acc + x.volume)
    transferVolume
  }
}

class HeadDaxTask(override val id: TaskId, override val name: String, children: List[DaxTask], workflowId: WorkflowId)
  extends DaxTask(id=id, name=name, execTime=0.0, children=children, workflowId=workflowId)

