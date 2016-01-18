package itmo.escience.simenv

/**
 * Created by Mishanya on 12.10.2015.
 */

/** This is the enter point into the simulator.
  */
object Main {
  def main(args: Array[String]) {

    // Указываем путь к файлу с вф или пайплайном
    val basepath = ".\\resources\\storm-pipelines\\"
    val wfName = "Test1_10"
    val wfPath = basepath + wfName + ".xml"
    // В utilities есть функция parseDAX, которая считывает файлы такого формата

    // сколько будет копий одного и того же вф
    val sweeps = 2

    // ресурсы описываются числом ядер и максимальным размером канала передачи данных
    val cores = 8
    val bandwidth = 100

    // запуск
    val storm = new StormSimulatedAnnealing(wfPath, sweeps, cores, bandwidth)
    storm.initialization()
    storm.runAlg()
  }
}
