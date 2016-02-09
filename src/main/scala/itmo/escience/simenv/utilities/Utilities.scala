package itmo.escience.simenv.utilities

import java.util.UUID


/**
 * Created by user on 27.11.2015.
 */
object Utilities {

  def generateId(): String = UUID.randomUUID().toString

}
