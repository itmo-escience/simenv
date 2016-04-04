package itmo.escience.simenv.simulator

/**
 * Created by user on 27.11.2015.
 */
trait Simulator[T, N] {

  def init()

  def runSimulation()

}
