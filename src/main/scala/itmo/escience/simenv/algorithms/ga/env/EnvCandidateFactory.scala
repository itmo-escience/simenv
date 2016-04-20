package itmo.escience.simenv.algorithms.ga.env

import java.util.Random

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.modelling.Environment
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory

/**
  * Created by mikhail on 26.01.2016.
  */
class EnvCandidateFactory[T <: Task, N <: Node](env: Environment[N], ctx: Context[T, N]) extends AbstractCandidateFactory[EnvConfSolution] {
  val _ctx = ctx
  def getCtx = ctx
  override def generateRandomCandidate(random: Random): EnvConfSolution = {
    var result: List[MappedEnv] = List[MappedEnv]()
    var fixedAvailNodes: List[MappedEnv] = List[MappedEnv]()
    val carriers = env.carriers
    for (carrier <- carriers) {

      val interceptors = scala.util.Random.shuffle(carrier.children.asInstanceOf[List[CapacityBasedNode]].filter(x => x.status == NodeStatus.UP))
      val totalCap: Double = interceptors.foldLeft(0.0)((s, x) => s + x.capacity)
      var curCap: Double = totalCap
      val it: Iterator[CapacityBasedNode] = interceptors.iterator
      while (it.hasNext) {
        val node = it.next()
        val nodeSched = ctx.schedule.getMap.get(node.id)
        if (ctx.schedule.getMap.containsKey(node.id) && nodeSched.nonEmpty && nodeSched.last.endTime > ctx.currentTime) {
          if (node.status == NodeStatus.UP) {
            fixedAvailNodes :+= new MappedEnv(node.id, node.capacity)
          }
          curCap -= node.capacity
        } else {
          if (!it.hasNext) {
            result :+= new MappedEnv(node.id, curCap)
          } else {
            if (curCap > 0) {
              val nodeCap = random.nextInt(curCap.toInt)
              curCap -= nodeCap
              result :+= new MappedEnv(node.id, nodeCap.toDouble)
            } else {
              result :+= new MappedEnv(node.id, 0.0)
            }
          }
        }
      }
    }
    new EnvConfSolution(result)
  }

}
