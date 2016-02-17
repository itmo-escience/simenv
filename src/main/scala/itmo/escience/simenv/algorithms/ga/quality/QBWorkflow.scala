package itmo.escience.simenv.algorithms.ga.quality

import itmo.escience.simenv.environment.entities.{TaskId, Task, WorkflowId, Workflow}

/**
  * Created by user on 17.02.2016.
  */
class QBWorkflow(id: WorkflowId,
                 name:String,
                 headTask:Task,
                 val tasksAlternative:Map[TaskId,List[Task]]) extends Workflow(id, name, headTask) {

  def buildAlternateWorkflow(replacers:List[(TaskId, Task)]): Workflow = {
    throw new NotImplementedError()
  }

}
