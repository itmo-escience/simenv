package itmo.escience.simenv

import itmo.escience.simenv.utilities.JSONParser

/**
 * Created by Mishanya on 12.10.2015.
 */

/** This is the enter point into the simulator.
  */
object Main {
  def main(args: Array[String]) {

    // Указываем путь к файлу с вф или пайплайном
    val wfPath = ".\\resources\\tplgs\\tplg1.json"
    val envPath = ".\\resources\\envs\\env1.json"
    val localNet = 5000
    val globNet = 5
    // запуск

    val env = JSONParser.parseEnv(envPath, globNet, localNet)
    val workload = JSONParser.parseWorkload(wfPath)

//    val storm = new StormSimulatedAnnealing(wfPath, envPath, globNet, localNet)
//    storm.initialization()
//    storm.runAlg()
//    val cpu = storm.getCpuUtilization(storm.schedule)
//    println(s"Usage: ${cpu}")
//    val transfer = storm.getTransfer(storm.schedule)
//    println(s"Transfer: ${transfer}")

    println("finish")
  }
}
