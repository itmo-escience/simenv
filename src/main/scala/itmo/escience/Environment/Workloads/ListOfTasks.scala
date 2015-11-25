package itmo.escience.Environment.Workloads

import itmo.escience.Environment.Entities.Task
import itmo.escience.Environment.Workload

/**
  * Created by Mishanya on 24.11.2015.
  */
class ListOfTasks extends Workload {
  var tasks: List[Task] = List()

  def setTasks(tasks: List[Task]) = {
    this.tasks = tasks
  }
}
