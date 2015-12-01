package itmo.escience.simenv.environment.entities

import com.sun.javaws.exceptions.InvalidArgumentException

/**
 * Created by Nikolay on 11/29/2015.
 */
class DaxTask(val id: TaskId, val name: String, val execTime: Double,
                   val inputData: List[DataFile] = List(),
                   val outputData: List[DataFile] = List(),
                   var parents: List[DaxTask] = List(),
                   var children: List[DaxTask] ) extends Task {
  override def status: TaskStatus = ???

  override def toString: String = {
    s"DaxTask id: ${id}"
  }
}

class HeadDaxTask(override val id: TaskId, override val name: String, children: List[DaxTask] )
  extends DaxTask(id=id, name=name, execTime=0.0, children=children)
