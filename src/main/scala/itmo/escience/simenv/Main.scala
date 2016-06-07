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
//    val wfPath = ".\\resources\\tplgs\\test1all.json"
    val wfPath = ".\\resources\\tplgs\\rnd1.json"
    val envPath = ".\\resources\\envs\\env2.json"

//    val wfPath = "[{\"megabytesOut\":30.0,\"children\":[\"2\",\"3\"],\"cpu.pcore.percent\":10.0,\"max.heap.size.mb\":128.0,\"name\":\"$checkpointspout\",\"megabytesPerSecond\":150.0,\"id\":\"0\",\"megabytesFromParent\":[],\"parents\":[]},{\"megabytesOut\":30.0,\"children\":[\"2\",\"3\"],\"cpu.pcore.percent\":120.0,\"max.heap.size.mb\":512.0,\"name\":\"spout\",\"megabytesPerSecond\":150.0,\"id\":\"1\",\"megabytesFromParent\":[],\"parents\":[]},{\"megabytesOut\":30.0,\"children\":[\"4\"],\"cpu.pcore.percent\":120.0,\"max.heap.size.mb\":512.0,\"name\":\"partialsum\",\"megabytesPerSecond\":150.0,\"id\":\"2\",\"megabytesFromParent\":[30.0,30.0],\"parents\":[\"0\",\"1\"]},{\"megabytesOut\":30.0,\"children\":[\"4\"],\"cpu.pcore.percent\":120.0,\"max.heap.size.mb\":512.0,\"name\":\"partialsum\",\"megabytesPerSecond\":150.0,\"id\":\"3\",\"megabytesFromParent\":[30.0,30.0],\"parents\":[\"0\",\"1\"]},{\"megabytesOut\":30.0,\"children\":[],\"cpu.pcore.percent\":180.0,\"max.heap.size.mb\":296.0,\"name\":\"printer\",\"megabytesPerSecond\":150.0,\"id\":\"4\",\"megabytesFromParent\":[30.0,30.0],\"parents\":[\"2\",\"3\"]},{\"name\":\"eventlogger\",\"id\":\"5\",\"children\":[],\"cpu.pcore.percent\":10.0,\"max.heap.size.mb\":128.0},{\"name\":\"acker\",\"id\":\"6\",\"children\":[],\"cpu.pcore.percent\":10.0,\"max.heap.size.mb\":128.0}]";
//    val envPath = "[{\"nodes\":[{\"availableCpuResources\":400.0,\"totalCpuResources\":400.0,\"id\":\"9b5c8002-944d-48e8-b5eb-caf4c78af95b\",\"totalMemoryResources\":3072.0,\"availableMemoryResources\":3072.0},{\"availableCpuResources\":400.0,\"totalCpuResources\":400.0,\"id\":\"9b5c8002-944d-48e8-b5eb-caf4c78af95b4\",\"totalMemoryResources\":3072.0,\"availableMemoryResources\":3072.0},{\"availableCpuResources\":400.0,\"totalCpuResources\":400.0,\"id\":\"9b5c8002-944d-48e8-b5eb-caf4c78af95b2\",\"totalMemoryResources\":3072.0,\"availableMemoryResources\":3072.0},{\"availableCpuResources\":400.0,\"totalCpuResources\":400.0,\"id\":\"9b5c8002-944d-48e8-b5eb-caf4c78af95b3\",\"totalMemoryResources\":3072.0,\"availableMemoryResources\":3072.0},{\"availableCpuResources\":400.0,\"totalCpuResources\":400.0,\"id\":\"b6957156-e364-4cc0-ad76-473471c97b3e\",\"totalMemoryResources\":3072.0,\"availableMemoryResources\":3072.0}],\"id\":\"Cluster0\"}]";

    val seedSolution1 = ".\\resources\\solutions\\sol1.json"
    val seedSolution12 = ".\\resources\\solutions\\sol2.json"
//    val seedSolution2 = ".\\resources\\solutions\\rnd3.json"
    val seeds = List[String](seedSolution1, seedSolution12)//, seedSolution12, seedSolution2)//, seedSolution2)



    val localNet = 1000000 // Bandwidth in rack in MB\sec
    val globNet = 1 // bandwidth between racks in MB\sec
    // запуск

    val storm = new StormScheduler(wfPath, envPath, globNet, localNet, seeds)
    storm.initialization()

    // Get result schedule
    val result = storm.run(needPrint=true)
    val fit = storm.runFit()
    println(fit)

    // Visualize schedule
    storm.drawSolution(StormSchedulingProblem.scheduleToSolution(result))



    println("finish")
  }
}
