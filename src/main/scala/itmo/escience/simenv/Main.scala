package itmo.escience.simenv

/**
 * Created by Mishanya on 12.10.2015.
 */

/** This is the enter point into the simulator.
  */
object Main {
  def main(args: Array[String]) {

    // Указываем путь к файлу с вф или пайплайном
//    val wfPath = ".\\resources\\tplgs\\tplg1.json"
//    val wfPath = "[{\"id\":\"checkpointspout\",\"children\":[\"partialsum\"],\"cpu.pcore.percent\":10.0,\"max.heap.size.mb\":128.0},\n{\"id\":\"spout\",\"children\":[\"partialsum\"],\"cpu.pcore.percent\":30.0,\"max.heap.size.mb\":512.0},\n{\"id\":\"partialsum\",\"children\":[\"printer\",\"printer\"],\"cpu.pcore.percent\":30.0,\"max.heap.size.mb\":512.0},\n{\"id\":\"printer\",\"children\":[\"total\",\"total\"],\"cpu.pcore.percent\":15.0,\"max.heap.size.mb\":296.0},\n{\"id\":\"printer\",\"children\":[\"total\",\"total\"],\"cpu.pcore.percent\":15.0,\"max.heap.size.mb\":296.0},\n{\"id\":\"total\",\"children\":[\"total2\",\"total2\"],\"cpu.pcore.percent\":10.0,\"max.heap.size.mb\":128.0},\n{\"id\":\"total2\",\"children\":[],\"cpu.pcore.percent\":10.0,\"max.heap.size.mb\":128.0},\n{\"id\":\"eventlogger\",\"children\":[],\"cpu.pcore.percent\":10.0,\"max.heap.size.mb\":128.0},\n{\"id\":\"acker\",\"children\":[],\"cpu.pcore.percent\":10.0,\"max.heap.size.mb\":128.0}]"
    val wfPath = ".\\resources\\tplgs\\tplg2.json"
    val wfPath = ".\\resources\\tplgs\\tplg2.json"
//    val wfPath = "[{\"id\":\"spout\",\"children\":[\"partialsum\"],\"cpu.pcore.percent\":10.0,\"max.heap.size.mb\":128.0},{\"id\":\"partialsum\",\"children\":[\"printer\"],\"cpu.pcore.percent\":10.0,\"max.heap.size.mb\":128.0},{\"id\":\"printer\",\"children\":[\"total\"],\"cpu.pcore.percent\":10.0,\"max.heap.size.mb\":128.0},{\"id\":\"total\",\"children\":[\"total2\"],\"cpu.pcore.percent\":10.0,\"max.heap.size.mb\":128.0},{\"id\":\"total2\",\"children\":[],\"cpu.pcore.percent\":10.0,\"max.heap.size.mb\":128.0}]"
//    val envPath = "[{\"availableCpuResources\":400.0,\"totalCpuResources\":400.0,\"totalMemoryResources\":3072.0,\"id\":\"8bf77b36-1d85-44b4-9527-b7b64bf4bf93\",\"availableMemoryResources\":3072.0},{\"availableCpuResources\":400.0,\"totalCpuResources\":400.0,\"totalMemoryResources\":3072.0,\"id\":\"5f363c95-673b-4612-baea-454f2842d728\",\"availableMemoryResources\":3072.0}]"
    val envPath = ".\\resources\\envs\\env2.json"

    // В utilities есть функция parseDAX, которая считывает файлы такого формата

    // Максимальный канал на расурсе
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
