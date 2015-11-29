package itmo.escience.simenv.environment.entities

import com.sun.javaws.exceptions.InvalidArgumentException

/**
 * Created by Nikolay on 11/29/2015.
 */
class DaxTask(val id: TaskId, val name: String, val execTime: Double,
                   val inputData: List[DataFile] = List(),
                   val outputData: List[DataFile] = List(),
                   val parents: List[DaxTask],
                   val children: List[DaxTask] ) extends Task
