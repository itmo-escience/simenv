package itmo.escience.simenv


/**
 * Created by Mishanya on 12.10.2015.
 */

/** This is the enter point into the simulator.
  */
object Main {
  def main(args: Array[String]) {

    import itmo.escience.simenv.utilities.Utilities

    val path = ".\\resources\\wf-examples\\Montage_25.xml"

    val wf = Utilities.parseDAX(path)

  //TODO:
    // 1. read parameters or config
    // 2. create instance or use object of appropriate Experiment
    // 3. Run experiment
    // 4. Exit
  }
}
