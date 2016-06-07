package itmo.escience.simenv.tstorm

import java.util
import java.util.Random

import itmo.escience.simenv.entities.{DaxTask, CpuRamNode, CarrierNodeEnvironment}
import itmo.escience.simenv.utilities.JSONParser

/**
  * Created by mikhail on 12.05.2016.
  */
object TstormAlg {

  def run(workloadPath: String, envPath: String, globNet: Int, localNet: Int) = {
    var env: CarrierNodeEnvironment[CpuRamNode] = null
    var tasks: util.HashMap[String, DaxTask] = null
    env = JSONParser.parseEnv(envPath, globNet, localNet)
    tasks = JSONParser.parseWorkload(workloadPath)

    val rnd: Random = new Random()

    runAlg(env, tasks)

  }

  def runAlg(env: CarrierNodeEnvironment[CpuRamNode], tasks: util.HashMap[String, DaxTask]) = {

  }

}
