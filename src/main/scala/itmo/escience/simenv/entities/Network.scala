package itmo.escience.simenv.entities

import itmo.escience.simenv.common.NameAndId

/**
 * Created by Nikolay on 11/29/2015.
 */
class Network(val id:String, val name: String, val bandwidth:Double, val nodes:Seq[Node]) extends NameAndId[String]
{
  override def equals(obj: scala.Any): Boolean = obj match {
    case x:Network => id.equals(x.id)
    case _ => super.equals(obj)
  }

  override def hashCode(): Int = id.hashCode()
}
