package itmo.escience.simenv.environment.entities

/**
 * Created by Nikolay on 11/29/2015.
 */
case class DaxTask(id: TaskId, name: String, execTime: Double,
                   inputData: List[DataFile] = List(),
                   outputData: List[DataFile] = List(),
                   parents: List[DaxTask],
                   children: List[DaxTask] ) extends Task
