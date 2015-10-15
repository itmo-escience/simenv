package itmo.escience.Environment.Entities

/**
 * Created by Mishanya on 14.10.2015.
 */
class Task(cName: String, cExecTime: Double) {
  val name: String = cName
  // Estimated execution time of this task on an average Node
  val execTime: Double = cExecTime
  //TODO add data dependencies
  //TODO change this class into treat and create children: Workflow, SimpleTask ...

}
