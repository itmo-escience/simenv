package itmo.escience.simenv.utilities

import java.io.File
import java.util.UUID

import itmo.escience.simenv.environment.entities._

import scala.xml.{Node, XML}

/**
 * Created by user on 27.11.2015.
 */
object Utilities {

  def generateId(): String = UUID.randomUUID().toString

}
