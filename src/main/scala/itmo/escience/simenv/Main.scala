package itmo.escience.simenv

import itmo.escience.simenv.ga.StormSchedulingProblem

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
    val seedSolution = ".\\resources\\solutions\\sol1.json"
    val localNet = 5000
    val globNet = 5
    // запуск

    val storm = new StormScheduler(wfPath, envPath, globNet, localNet, seedSolution)
    storm.initialization()
    // Get result schedule
    val result = storm.run()

    // Visualize schedule
    storm.drawSolution(StormSchedulingProblem.scheduleToSolution(result))

    println("finish")
  }
}
