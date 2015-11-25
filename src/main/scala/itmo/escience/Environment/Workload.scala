package itmo.escience.Environment

import itmo.escience.Environment.Entities.Task

/**
  * Created by Mishanya on 24.11.2015.
  */
trait Workload {
  var tasks: List[Task]

  def setTasks(tasks: List[Task])
}
