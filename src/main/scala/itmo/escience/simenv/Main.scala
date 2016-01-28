package itmo.escience.simenv

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

    // В utilities есть функция parseDAX, которая считывает файлы такого формата

    // сколько будет копий одного и того же вф
//    val sweeps = 2

    // ресурсы описываются числом ядер и максимальным размером канала передачи данных
//    val cores = 8
    val bandwidth = 1025

    // запуск
    val storm = new StormSimulatedAnnealing(wfPath, envPath, bandwidth)
    storm.initialization()
    storm.runAlg()
    val cpu = storm.getCpuUtilization(storm.schedule)
    println(s"Usage: ${cpu}")
    val transfer = storm.getTransfer(storm.schedule)
    println(s"Transfer: ${transfer}")
  }
}
