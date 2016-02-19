package itmo.escience.simenv.ga
import scala.collection.JavaConversions._

/**
  * Created by mikhail on 09.02.2016.
  */
object StormSchedulingProblem {
//  def repairMap(map: java.util.HashMap[String, (String, Double)]): java.util.HashMap[String, (String, Double)] = {
//    val newMap: java.util.HashMap[String, (String, Double)] = new java.util.HashMap[String, (String, Double)]()
//    val usedProc: java.util.HashMap[String, Double] = new java.util.HashMap[String, Double]()
//    for (k <- map.keySet()) {
//      val item = map.get(k)
//      if (!usedProc.containsKey(item._1)) {
//        usedProc.put(item._1, 0)
//      }
//      usedProc.put(item._1, usedProc.get(item._1) + item._2)
//    }
//    for (k <- map.keySet()) {
//      val item = map.get(k)
//      val proc = usedProc.get(item._1)
//      if (proc > 1) {
//        newMap.put(k, (item._1, item._2 / proc))
//      } else {
//        newMap.put(k, (item._1, item._2))
//      }
//    }
//    newMap
//  }

  def mapToString(map: java.util.HashMap[String, String]) = {
    var result = ""
    for (t <- map.keySet()) {
      val item = map.get(t)
      result += s"$t: node=$item\n"
    }
    result
  }

  def solutionToSchedule(sol: SSSolution): java.util.HashMap[String, List[String]] = {
    val genes = sol.genes
    val result = new java.util.HashMap[String, List[String]]()
    for (item <- genes) {
      val task = item._1
      val node = item._2
      if (!result.containsKey(node)) {
        result.put(node, List[String]())
      }
      result.put(node, result.get(node) :+ task)
    }
    result
  }

  def scheduleToSolution(schedule: java.util.HashMap[String, List[String]]): SSSolution = {
    val result = new java.util.HashMap[String, String]()
    for (item <- schedule) {
      val node = item._1
      for (t <- item._2) {
        val task = t
        result.put(task, node)
      }
    }
    new SSSolution(result)
  }
}
