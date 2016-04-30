package itmo.escience.simenv.algorithms.ultraGA

import java.util
import java.util.{Random}

import org.uncommons.watchmaker.framework.EvaluatedCandidate
import org.uncommons.watchmaker.framework.operators.AbstractCrossover
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection

import scala.collection.JavaConversions._

/**
  * Created by mikhail on 22.01.2016.
  */
class MishanyaScheduleCrossoverOperator(crossoverProb: Double, crossoverPoints: Int = 1)
  extends AbstractCrossover[MishanyaSolution](crossoverPoints){

  override def apply(parents: util.List[MishanyaSolution], random: Random): util.List[MishanyaSolution] = {

    val selector = new RouletteWheelSelection()

    val result: util.ArrayList[MishanyaSolution] = new util.ArrayList[MishanyaSolution]()

    val evalParents = parents.map(x => new EvaluatedCandidate[MishanyaSolution](x, x.fitness))

    for (i <- 0 until (parents.size * crossoverProb).toInt) {
      val curParents = selector.select(evalParents, false, 2, random)
      val p1 = curParents.get(0).asInstanceOf[MishanyaSolution].copy
      val p2 = curParents.get(1).asInstanceOf[MishanyaSolution].copy
        result.addAll(mate666(p1, p2, random))
    }
    result
  }

  def mate666(p1: MishanyaSolution, p2: MishanyaSolution, rnd: Random): util.List[MishanyaSolution] = {
    var child1 = List[MMappedTask]()
    var child2 = List[MMappedTask]()
    val size = p1.getNumberOfVariables

    for (i <- 0 until size) {
      val p1gen = p1.getVariableValue(i)
      val p1task = p1gen.taskId
      val p1node = p1gen.nodeId
      val p2gen = p2.genSeq.filter(x => x.taskId == p1task).head
      val p2node = p2gen.nodeId
      var cNode: String = null
      if (rnd.nextBoolean()) {
        cNode = p1node
      } else {
        cNode = p2node
      }
      child1 :+= new MMappedTask(p1task, cNode)
    }

    for (i <- 0 until size) {
      val p2gen = p2.getVariableValue(i)
      val p2task = p2gen.taskId
      val p2node = p2gen.nodeId
      val p1gen = p1.genSeq.filter(x => x.taskId == p2task).head
      val p1node = p1gen.nodeId
      var cNode: String = null
      if (rnd.nextBoolean()) {
        cNode = p1node
      } else {
        cNode = p2node
      }
      child2 :+= new MMappedTask(p2task, cNode)
    }

    val c1 = new MishanyaSolution(child1)
    val c2 = new MishanyaSolution(child2)

    val res = new util.ArrayList[MishanyaSolution](2)
    res.add(c1)
    res.add(c2)

    res
  }


  override def mate(p1: MishanyaSolution, p2: MishanyaSolution, points: Int, rnd: Random): util.List[MishanyaSolution] = {
    val size = p1.getNumberOfVariables
    val (left, right) = leftAndRight(size, rnd)

    val genesToBeReplaced_1 = (left until right).map(i => (p1.getVariableValue(i).taskId, i)).toMap
    val genesToBeReplaced_2 = (0 until p2.getNumberOfVariables).map(i => (p2.getVariableValue(i).taskId, i)).
      filter({case (taskId, i)=> genesToBeReplaced_1.contains(taskId)}).toMap

    for ((taskId, i) <- genesToBeReplaced_1) {
      val j = genesToBeReplaced_2(taskId)
      val node_1 = p1.getVariableValue(i).nodeId
      val node_2 = p2.getVariableValue(j).nodeId
      p1.setVariableValue(i, MMappedTask(taskId, node_2))
      p2.setVariableValue(j, MMappedTask(taskId, node_1))
    }

    val res = new util.ArrayList[MishanyaSolution](2)
    p1.evaluated = false
    p2.evaluated = false
    res.add(p1)
    res.add(p2)

    res
  }

  def mate2(p1: MishanyaSolution, p2: MishanyaSolution, points: Int, rnd: Random): util.List[MishanyaSolution] = {
    val size = p1.getNumberOfVariables
    val (left, right) = leftAndRight(size, rnd)

    var child1: List[MMappedTask] = List[MMappedTask]()
    var child2: List[MMappedTask] = List[MMappedTask]()

    val p1S = p1.getNumberOfVariables
    val p2S = p2.getNumberOfVariables

    val genes111 = (0 until left).map(i => (p1.getVariableValue(i).taskId, i)).toList
    val genes112 = (right until p1S).map(i => (p1.getVariableValue(i).taskId, i)).toList
    val genes11 = genes111 ++ genes112
    val genes21 = (0 until p2S).map(i => (p2.getVariableValue(i).taskId, i)).
      filter({case (taskId, i)=> !genes11.map(y => y._1).contains(taskId)}).toList


    val genes221 = (0 until left).map(i => (p2.getVariableValue(i).taskId, i)).toList
    val genes222 = (right until p2S).map(i => (p2.getVariableValue(i).taskId, i)).toList
    val genes22 = genes221 ++ genes222
    val genes12 = (0 until p1S).map(i => (p1.getVariableValue(i).taskId, i)).
      filter({case (taskId, i)=> !genes22.map(y => y._1).contains(taskId)}).toList

    for (it <- genes111) {
      val item = p1.getVariableValue(it._2)
      child1 :+= new MMappedTask(item.taskId, item.nodeId)
    }
    for (it <- genes21) {
      val item = p2.getVariableValue(it._2)
      child1 :+= new MMappedTask(item.taskId, item.nodeId)
    }
    for (it <- genes112) {
      val item = p1.getVariableValue(it._2)
      child1 :+= new MMappedTask(item.taskId, item.nodeId)
    }
    //child2
    for (it <- genes221) {
      val item = p2.getVariableValue(it._2)
      child2 :+= new MMappedTask(item.taskId, item.nodeId)
    }
    for (it <- genes12) {
      val item = p1.getVariableValue(it._2)
      child2 :+= new MMappedTask(item.taskId, item.nodeId)
    }
    for (it <- genes222) {
      val item = p2.getVariableValue(it._2)
      child2 :+= new MMappedTask(item.taskId, item.nodeId)
    }
    val res = new util.ArrayList[MishanyaSolution](2)
    res.add(new MishanyaSolution(child1))
    res.add(new MishanyaSolution(child2))

    res
  }

  private def leftAndRight(size: Int, rnd: Random) = {
    val a = rnd.nextInt(size)
    val b = rnd.nextInt(size)
    val left = Math.min(a, b)
    val right = Math.max(a,b)
    (left, right)
  }


}
