package itmo.escience.simenv.environment.entities

import com.sun.javaws.exceptions.InvalidArgumentException
import org.apache.commons.lang3.builder.HashCodeBuilder

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

  //TODO: reanimate this later
//  override def equals(obj: scala.Any): Boolean = obj match {
//    case x:DaxTask => id.equals(x.id) && name.equals(x.name) &&
//      execTime.equals(x.execTime) && inputData.equals(x.inputData) && outputData.equals(x.outputData) &&
//      parents.equals(x.parents) && children.equals(x.children)
//    case _ => false
//  }
//
//  // http://stackoverflow.com/questions/27581/what-issues-should-be-considered-when-overriding-equals-and-hashcode-in-java
//  override def hashCode(): Int = new HashCodeBuilder(13, 51). // two randomly chosen prime numbers
//    //appendSuper(super.hashCode()). //it needs if the class is deriving from base class
//    append(id).append(name).append(execTime).
//    append(inputData).append(outputData).
//    append(parents).append(children).toHashCode
}

class HeadDaxTask(override val id: TaskId, override val name: String, children: List[DaxTask] )
  extends DaxTask(id=id, name=name, execTime=0.0, children=children)
