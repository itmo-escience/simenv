package itmo.escience.simenv.simulator

import itmo.escience.simenv.simulator.events.Event

/**
 * Created by user on 27.11.2015.
 */
trait Simulator[T, N] {

  def init()

  def runSimulation()

}
