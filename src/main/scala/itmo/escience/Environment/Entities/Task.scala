package itmo.escience.Environment.Entities

/**
 * Created by Mishanya on 14.10.2015.
 */
class Task(cName: String, cExecTime: Double, cFiles: List[DataFile] = List(), cOutput: List[DataFile] = List()) {
  val name: String = cName
  // Estimated execution time of this task on an average Node
  val execTime: Double = cExecTime
  // Required input data
  val inputData: List[DataFile] = cFiles
  // Output data
  val outputData: List[DataFile] = cOutput
  //TODO change this class into treat and create children: Workflow, SimpleTask ...

}
