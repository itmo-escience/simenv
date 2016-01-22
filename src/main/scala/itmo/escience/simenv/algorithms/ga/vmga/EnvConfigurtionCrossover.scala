//package itmo.escience.simenv.algorithms.ga.vmga
//
//import java.util
//
//import itmo.escience.simenv.environment.entities.{Carrier, CpuTimeNode, DaxTask, Context}
//import org.uma.jmetal.operator.CrossoverOperator
//import org.uma.jmetal.util.JMetalException
//
//import scala.util.Random
//
///**
// * Created by user on 02.12.2015.
// */
//class EnvConfigurtionCrossover(probability: Double, context:Context[DaxTask, CpuTimeNode]) extends CrossoverOperator[EnvConfigurationSolution]{
//
//  private val random = new Random(System.currentTimeMillis)
//
//  /**
//   * Crossover creates new individuals and don't modify existed parents
//   * @param source list of parents
//   * @return list of children
//   */
//  override def execute(source: util.List[EnvConfigurationSolution]): util.List[EnvConfigurationSolution] = {
//
//    if (null == source) {
//      throw new JMetalException("Null parameter")
//    } else if (source.size() != 2) {
//      throw new JMetalException("There must be two parents instead of " + source.size())
//    }
//
//    if (random.nextDouble() <= probability) {
//
//      val p1 = source.get(0).copy()
//      val p2 = source.get(1).copy()
//
//      doCrossover(p1, p2)
//    }
//    else source
//  }
//
//  private def doCrossover(p1: EnvConfigurationSolution, p2: EnvConfigurationSolution) ={
//
//    val nodes = context.environment.carriers
//    if (nodes.size > 1) {
//      val n1 = nodes(random.nextInt(nodes.size))
//      val n2 = nodes.filter(x => x.id != n1.id)(random.nextInt(nodes.size - 1))
//      val n1vms = n1.asInstanceOf[Carrier[CpuTimeNode]].children.map(x => x.id)
//      val n2vms = n2.asInstanceOf[Carrier[CpuTimeNode]].children.map(x => x.id)
//      val p1n1 = p1.vmsSeq().filter(x => n1vms.contains(x.vmId))
//      val p1n2 = p1.vmsSeq().filter(x => n2vms.contains(x.vmId))
//      val p2n1 = p2.vmsSeq().filter(x => n1vms.contains(x.vmId))
//      val p2n2 = p2.vmsSeq().filter(x => n2vms.contains(x.vmId))
//      p1.addDeleteItems(p1n1, p2n1)
//      p2.addDeleteItems(p2n2, p1n2)
//    }
//
//    val ret = new util.ArrayList[EnvConfigurationSolution](2)
//    ret.add(p1)
//    ret.add(p2)
//
//    ret
//  }
//
//}
