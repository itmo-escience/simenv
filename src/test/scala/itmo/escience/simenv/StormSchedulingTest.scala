package itmo.escience.simenv

import itmo.escience.simenv.utilities.Utilities._
import org.junit.Test

/**
  * Created by Mishanya on 23.12.2015.
  */

@Test
class StormSchedulingTest {
  // single pipeline
  val basepath = ".\\resources\\storm-pipelines\\"
  val wfName = "Test1_3"
  val wf = parseDAX(basepath + wfName + ".xml")

  val pipelines = 3

  val cores = 8
  val bandwidth = 100

  @Test
  def testRun() = {
    println(wf.name)
    val storm = StormSimulatedAnnealing()
  }

}
