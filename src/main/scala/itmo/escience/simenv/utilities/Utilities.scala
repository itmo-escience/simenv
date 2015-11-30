package itmo.escience.simenv.utilities

import java.io.File
import java.util.UUID

import itmo.escience.simenv.environment.entities._
import sun.reflect.generics.reflectiveObjects.NotImplementedException

import scala.xml
import scala.xml.XML

/**
 * Created by user on 27.11.2015.
 */
object Utilities {

  def parseDAX(path:String): Workflow = {
    val file = new File(path)
    val dax = XML.loadFile(file)
    null
  }

  def generateId(): String = UUID.randomUUID().toString



}
