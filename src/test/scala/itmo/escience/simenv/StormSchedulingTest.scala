package itmo.escience.simenv

import itmo.escience.simenv.utilities.Utilities._
import itmo.escience.simenv.algorithms.storm.StormSimulatedAnnealing
import org.junit.Test

/**
  * Created by Mishanya on 23.12.2015.
  */

@Test
class StormSchedulingTest {
  // single pipeline
  val basepath = ".\\resources\\storm-pipelines\\"
  val wfName = "Test1_3"
  val wfPath = basepath + wfName + ".xml"

  val pipelines = 3

  val cores = 8
  val bandwidth = 30

  @Test
  def testRun() = {
    val storm = new StormSimulatedAnnealing(wfPath, pipelines, cores, bandwidth)
    storm.initialization()
    storm.runAlg()
  }

}
