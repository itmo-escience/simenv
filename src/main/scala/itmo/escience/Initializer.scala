package itmo.escience

/**
 * Created by Mishanya on 12.10.2015.
 */

/** This is the enter point into the simulator.
  */
object Initializer {
  def main(args: Array[String]) {

    val gui: Boolean = false

    var executor: Executor = null
    if (!gui) {
      executor = new ConsoleExecutor()
    } else {
      //TODO add gui executor
    }
  }
}
