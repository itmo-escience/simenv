package itmo.escience.simenv.utilities

import java.util

import itmo.escience.simenv.environment.entities._
import org.uncommons.maths.random.MersenneTwisterRNG
import scala.collection.JavaConversions._

/**
  * Created by mikhail on 26.04.2016.
  */
object RandomWFGenerator {

  val rnd = new MersenneTwisterRNG()

  def rand(b: Int): Double = {
    rnd.nextInt(b).toDouble
  }

  def randN(m: Double, v: Double): Double = {
    var res = math.round(rnd.nextGaussian() * v + m).toDouble
    if (res <= 0) {
      res = 1.0
    }
    res
  }

  // Generate wf from map
  def generateWf(wfMap: util.HashMap[String, (Double, List[(String, Double)])], name: String): Workflow = {
    val ids = wfMap.keySet()

    val allChildren = wfMap.foldLeft(List[String]())((s, x) => s ++ x._2._2.map(y => y._1)).distinct
    val parents = ids.filter(x => !allChildren.contains(x))

    val allDataDependencies = wfMap.foldLeft(List[(String, String, Double)]())((s, x) =>
      s ++ x._2._2.map(y => (x._1, y._1, y._2)))

    val dataMap = new util.HashMap[String, DataFile]()
    for (item <- allDataDependencies) {
      val dId = item._1 + "_" + item._2
      val data = new DataFile(id=dId, name=dId, volume=item._3)
      dataMap.put(dId, data)
    }

    val taskMap = new util.HashMap[String, DaxTask]()

    for (id <- ids) {
      val item = wfMap.get(id)

      var input = List[DataFile]()
      // find all parent data dependencies
      val taskParents = allDataDependencies.filter(x => x._2 == id)
      if (taskParents.nonEmpty) {
        input = taskParents.foldLeft(List[DataFile]())((s, x) => s :+ dataMap.get(x._1 + "_" + x._2))
      }

      var output = List[DataFile]()
      // find all children data
      val taskChildren = allDataDependencies.filter(x => x._1 == id)
      if (taskChildren.nonEmpty) {
        output = taskChildren.foldLeft(List[DataFile]())((s, x) => s :+ dataMap.get(x._1 + "_" + x._2))
      }

      val task = new DaxTask(id = id, name = id, execTime = item._1,
        inputData = input,
        outputData = output,
        parents = List[DaxTask](),
        children = List[DaxTask]())

      taskMap.put(id, task)
    }

    for (edge <- allDataDependencies) {
      val from = taskMap.get(edge._1)
      val to = taskMap.get(edge._2)
      from.children :+= to
      to.parents :+= from
    }

    val headTasks = parents.map(x => taskMap.get(x))
    val headTask = new HeadDaxTask(id="head", name="head", children=headTasks.toList)
    for (h <- headTasks) {
      h.parents :+= headTask
    }
    val wf = new Workflow(id=name, name=name, headTask=headTask)
    wf
  }


  // 2 -> 3 -> 2 -> 3
  def type1Generate(): util.HashMap[String, (Double, List[(String, Double)])] = {
    val result = new util.HashMap[String, (Double, List[(String, Double)])]()

    val l1R = rand(100)
    val l1Rv = l1R * 0.2
    val l1T = rand(1000)
    val l1Tv = l1T * 0.2

    result.put("t1", (randN(l1R, l1Rv),
      List[(String, Double)](
        ("t3", randN(l1T, l1Tv)),
        ("t4", randN(l1T, l1Tv))
      )))
    result.put("t2", (randN(l1R, l1Rv),
      List[(String, Double)](
        ("t4", randN(l1T, l1Tv)),
        ("t5", randN(l1T, l1Tv))
      )))

    val l2R = rand(100)
    val l2Rv = l2R * 0.2
    val l2T = rand(1000)
    val l2Tv = l2T * 0.2

    result.put("t3", (randN(l2R, l2Rv),
      List[(String, Double)](
        ("t6", randN(l2T, l2Tv))
      )))
    result.put("t4", (randN(l2R, l2Rv),
      List[(String, Double)](
        ("t6", randN(l2T, l2Tv)),
        ("t7", randN(l2T, l2Tv))
      )))
    result.put("t5", (randN(l2R, l2Rv),
      List[(String, Double)](
        ("t7", randN(l2T, l2Tv))
      )))

    val l3R = rand(100)
    val l3Rv = l3R * 0.2
    val l3T = rand(1000)
    val l3Tv = l3T * 0.2

    result.put("t6", (randN(l3R, l3Rv),
      List[(String, Double)](
        ("t8", randN(l3T, l3Tv)),
        ("t9", randN(l3T, l3Tv))
      )))
    result.put("t7", (randN(l3R, l3Rv),
      List[(String, Double)](
        ("t9", randN(l3T, l3Tv)),
        ("t10", randN(l3T, l3Tv))
      )))

    val l4R = rand(100)
    val l4Rv = l4R * 0.2

    result.put("t8", (randN(l4R, l4Rv),
      List[(String, Double)](
      )))
    result.put("t9", (randN(l4R, l4Rv),
      List[(String, Double)](
      )))
    result.put("t10", (randN(l4R, l4Rv),
      List[(String, Double)](
      )))

    result
  }

  // fixed 2 -> 3 -> 2 -> 3
  def type1FixedGenerate(): util.HashMap[String, (Double, List[(String, Double)])] = {
    val result = new util.HashMap[String, (Double, List[(String, Double)])]()

    result.put("t1", (randN(10, 2),
      List[(String, Double)](
        ("t3", randN(800, 100)),
        ("t4", randN(800, 100))
      )))
    result.put("t2", (randN(10, 2),
      List[(String, Double)](
        ("t4", randN(800, 100)),
        ("t5", randN(800, 100))
      )))

    result.put("t3", (randN(15, 5),
      List[(String, Double)](
        ("t6", randN(85, 30))
      )))
    result.put("t4", (randN(15, 5),
      List[(String, Double)](
        ("t6", randN(85, 30)),
        ("t7", randN(85, 30))
      )))
    result.put("t5", (randN(15, 5),
      List[(String, Double)](
        ("t7", randN(85, 30))
      )))

    result.put("t6", (randN(40, 10),
      List[(String, Double)](
        ("t8", randN(800, 200)),
        ("t9", randN(800, 200))
      )))
    result.put("t7", (randN(40, 10),
      List[(String, Double)](
        ("t9", randN(800, 200)),
        ("t10", randN(800, 200))
      )))

    result.put("t8", (randN(6, 2),
      List[(String, Double)](
      )))
    result.put("t9", (randN(6, 2),
      List[(String, Double)](
      )))
    result.put("t10", (randN(6, 2),
      List[(String, Double)](
      )))

    result
  }

  // 2 -> 4 -> 3 -> 2 -> 1 with statistics
  def type2Generate(): util.HashMap[String, (Double, List[(String, Double)])] = {
    val result = new util.HashMap[String, (Double, List[(String, Double)])]()

    val lsR = rand(5)
    val lsRv = 1
    val lsT = rand(5)
    val lsTv = 1

    val l1R = rand(100)
    val l1Rv = l1R * 0.2
    val l1T = rand(1000)
    val l1Tv = l1T * 0.2

    result.put("t1", (randN(l1R, l1Rv),
      List[(String, Double)](
        ("t3", randN(l1T, l1Tv)),
        ("t4", randN(l1T, l1Tv)),
        ("t5", randN(l1T, l1Tv)),
        ("s1", randN(lsT, lsTv))
      )))
    result.put("t2", (randN(l1R, l1Rv),
      List[(String, Double)](
        ("t4", randN(l1T, l1Tv)),
        ("t5", randN(l1T, l1Tv)),
        ("t6", randN(l1T, l1Tv)),
        ("s1", randN(lsT, lsTv))
      )))

    val l2R = rand(100)
    val l2Rv = l2R * 0.2
    val l2T = rand(1000)
    val l2Tv = l2T * 0.2

    result.put("t3", (randN(l2R, l2Rv),
      List[(String, Double)](
        ("t7", randN(l2T, l2Tv)),
        ("s2", randN(lsT, lsTv))
      )))
    result.put("t4", (randN(l2R, l2Rv),
      List[(String, Double)](
        ("t7", randN(l2T, l2Tv)),
        ("t8", randN(l2T, l2Tv)),
        ("s2", randN(lsT, lsTv))
      )))
    result.put("t5", (randN(l2R, l2Rv),
      List[(String, Double)](
        ("t8", randN(l2T, l2Tv)),
        ("t9", randN(l2T, l2Tv)),
        ("s2", randN(lsT, lsTv))
      )))
    result.put("t6", (randN(l2R, l2Rv),
      List[(String, Double)](
        ("t9", randN(l2T, l2Tv)),
        ("s2", randN(lsT, lsTv))
      )))

    val l3R = rand(100)
    val l3Rv = l3R * 0.2
    val l3T = rand(1000)
    val l3Tv = l3T * 0.2

    result.put("t7", (randN(l3R, l3Rv),
      List[(String, Double)](
        ("t10", randN(l3T, l3Tv)),
        ("s3", randN(lsT, lsTv))
      )))
    result.put("t8", (randN(l3R, l3Rv),
      List[(String, Double)](
        ("t10", randN(l3T, l3Tv)),
        ("t11", randN(l3T, l3Tv)),
        ("s3", randN(lsT, lsTv))
      )))
    result.put("t9", (randN(l3R, l3Rv),
      List[(String, Double)](
        ("t11", randN(l3T, l3Tv)),
        ("s3", randN(lsT, lsTv))
      )))


    val l4R = rand(100)
    val l4Rv = l4R * 0.2
    val l4T = rand(1000)
    val l4Tv = l4T * 0.2


    result.put("t10", (randN(l4R, l4Rv),
      List[(String, Double)](
        ("t12", randN(l4T, l4Tv)),
        ("s4", randN(lsT, lsTv))
      )))
    result.put("t11", (randN(l4R, l4Rv),
      List[(String, Double)](
        ("t12", randN(l4T, l4Tv)),
        ("s4", randN(lsT, lsTv))
      )))

    val l5R = rand(100)
    val l5Rv = l5R * 0.2

    result.put("t12", (randN(l5R, l5Rv),
      List[(String, Double)](
      )))

    result.put("s1", (randN(lsR, lsRv),
      List[(String, Double)](
      )))
    result.put("s2", (randN(lsR, lsRv),
      List[(String, Double)](
      )))
    result.put("s3", (randN(lsR, lsRv),
      List[(String, Double)](
      )))
    result.put("s4", (randN(lsR, lsRv),
      List[(String, Double)](
      )))

    result
  }

  // Hydro sweeps
  def type3Generate(): util.HashMap[String, (Double, List[(String, Double)])] = {
    val result = new util.HashMap[String, (Double, List[(String, Double)])]()

    val cyclgenCalc = 325.0
    val cyclgenCalcV = 50.0
    val cyclgenSwanTr = 100.0
    val cyclgenSwanTrV = 10.0
    val cyclgenBsmTr = 100.0
    val cyclgenBsmTrV = 10.0

    val swanCalc = 1300.0
    val swanCalcV = 100.0
    val swanBsmTr = 30.0
    val swanBsmTrV = 5.0

    val bsmCalc = 2800.0
    val bsmCalcV = 300.0
    val bsmCcdTr = 9.0
    val bsmCcdTrV = 2.0

    val ccdCalc = 1.0
    val ccdCalcV = 0.1
    val ccdResTr = 10.0
    val ccdResTrV = 2.0

    val resCalc = 1.0
    val resCalcV = 0.1

    // sweep 1
    result.put("cone1", (randN(cyclgenCalc, cyclgenCalcV),
      List[(String, Double)](
        ("swan1", randN(cyclgenSwanTr, cyclgenSwanTrV)),
        ("bsm1", randN(cyclgenBsmTr, cyclgenBsmTrV))
      )))

    result.put("swan1", (randN(swanCalc, swanCalcV),
      List[(String, Double)](
        ("bsm1", randN(swanBsmTr, swanBsmTrV))
      )))

    result.put("bsm1", (randN(bsmCalc, bsmCalcV),
      List[(String, Double)](
        ("ccd1", randN(bsmCcdTr, bsmCcdTrV))
      )))

    result.put("ccd1", (randN(ccdCalc, ccdCalcV),
      List[(String, Double)](
        ("res", randN(ccdResTr, ccdResTrV))
      )))

    // sweep 2
    result.put("cone2", (randN(cyclgenCalc, cyclgenCalcV),
      List[(String, Double)](
        ("swan2", randN(cyclgenSwanTr, cyclgenSwanTrV)),
        ("bsm2", randN(cyclgenBsmTr, cyclgenBsmTrV))
      )))

    result.put("swan2", (randN(swanCalc, swanCalcV),
      List[(String, Double)](
        ("bsm2", randN(swanBsmTr, swanBsmTrV))
      )))

    result.put("bsm2", (randN(bsmCalc, bsmCalcV),
      List[(String, Double)](
        ("ccd2", randN(bsmCcdTr, bsmCcdTrV))
      )))

    result.put("ccd2", (randN(ccdCalc, ccdCalcV),
      List[(String, Double)](
        ("res", randN(ccdResTr, ccdResTrV))
      )))

    // sweep 3
    result.put("cone3", (randN(cyclgenCalc, cyclgenCalcV),
      List[(String, Double)](
        ("swan3", randN(cyclgenSwanTr, cyclgenSwanTrV)),
        ("bsm3", randN(cyclgenBsmTr, cyclgenBsmTrV))
      )))

    result.put("swan3", (randN(swanCalc, swanCalcV),
      List[(String, Double)](
        ("bsm3", randN(swanBsmTr, swanBsmTrV))
      )))

    result.put("bsm3", (randN(bsmCalc, bsmCalcV),
      List[(String, Double)](
        ("ccd3", randN(bsmCcdTr, bsmCcdTrV))
      )))

    result.put("ccd3", (randN(ccdCalc, ccdCalcV),
      List[(String, Double)](
        ("res", randN(ccdResTr, ccdResTrV))
      )))


    // Result aggregation
    result.put("res", (randN(resCalc, resCalcV),
      List[(String, Double)](
      )))

    result
  }

  // Hydro sweeps
  def type3GAGenerate(transfers: Array[Int]): util.HashMap[String, (Double, List[(String, Double)])] = {
    val result = new util.HashMap[String, (Double, List[(String, Double)])]()

    val cyclgenCalc = 325.0
    val cyclgenCalcV = 50.0
    val cyclgenSwanTr = transfers(0)
    val cyclgenSwanTrV = cyclgenSwanTr * 0.1
    val cyclgenBsmTr = transfers(1)
    val cyclgenBsmTrV = cyclgenBsmTr * 0.1

    val swanCalc = 1300.0
    val swanCalcV = 100.0
    val swanBsmTr = transfers(2)
    val swanBsmTrV = swanBsmTr * 0.1

    val bsmCalc = 2800.0
    val bsmCalcV = 300.0
    val bsmCcdTr = transfers(3)
    val bsmCcdTrV = bsmCcdTr * 0.1

    val ccdCalc = 1.0
    val ccdCalcV = 0.1
    val ccdResTr = transfers(4)
    val ccdResTrV = ccdResTr * 0.1

    val resCalc = 1.0
    val resCalcV = 0.1

    // sweep 1
    result.put("cone1", (randN(cyclgenCalc, cyclgenCalcV),
      List[(String, Double)](
        ("swan1", randN(cyclgenSwanTr, cyclgenSwanTrV)),
        ("bsm1", randN(cyclgenBsmTr, cyclgenBsmTrV))
      )))

    result.put("swan1", (randN(swanCalc, swanCalcV),
      List[(String, Double)](
        ("bsm1", randN(swanBsmTr, swanBsmTrV))
      )))

    result.put("bsm1", (randN(bsmCalc, bsmCalcV),
      List[(String, Double)](
        ("ccd1", randN(bsmCcdTr, bsmCcdTrV))
      )))

    result.put("ccd1", (randN(ccdCalc, ccdCalcV),
      List[(String, Double)](
        ("res", randN(ccdResTr, ccdResTrV))
      )))

    // sweep 2
    result.put("cone2", (randN(cyclgenCalc, cyclgenCalcV),
      List[(String, Double)](
        ("swan2", randN(cyclgenSwanTr, cyclgenSwanTrV)),
        ("bsm2", randN(cyclgenBsmTr, cyclgenBsmTrV))
      )))

    result.put("swan2", (randN(swanCalc, swanCalcV),
      List[(String, Double)](
        ("bsm2", randN(swanBsmTr, swanBsmTrV))
      )))

    result.put("bsm2", (randN(bsmCalc, bsmCalcV),
      List[(String, Double)](
        ("ccd2", randN(bsmCcdTr, bsmCcdTrV))
      )))

    result.put("ccd2", (randN(ccdCalc, ccdCalcV),
      List[(String, Double)](
        ("res", randN(ccdResTr, ccdResTrV))
      )))

    // sweep 3
    result.put("cone3", (randN(cyclgenCalc, cyclgenCalcV),
      List[(String, Double)](
        ("swan3", randN(cyclgenSwanTr, cyclgenSwanTrV)),
        ("bsm3", randN(cyclgenBsmTr, cyclgenBsmTrV))
      )))

    result.put("swan3", (randN(swanCalc, swanCalcV),
      List[(String, Double)](
        ("bsm3", randN(swanBsmTr, swanBsmTrV))
      )))

    result.put("bsm3", (randN(bsmCalc, bsmCalcV),
      List[(String, Double)](
        ("ccd3", randN(bsmCcdTr, bsmCcdTrV))
      )))

    result.put("ccd3", (randN(ccdCalc, ccdCalcV),
      List[(String, Double)](
        ("res", randN(ccdResTr, ccdResTrV))
      )))


    // Result aggregation
    result.put("res", (randN(resCalc, resCalcV),
      List[(String, Double)](
      )))

    result
  }

}
