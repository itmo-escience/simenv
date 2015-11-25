package itmo.escience

import itmo.escience.Executors.{Executor, ConsoleExecutor}

/**
 * Created by Mishanya on 12.10.2015.
 */

/** This is the enter point into the simulator.
  */
object Main {
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
