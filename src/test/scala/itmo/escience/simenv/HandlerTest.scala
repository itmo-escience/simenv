package itmo.escience.simenv

import itmo.escience.simenv.environment.entities.{Context, DataFile, DaxTask, Node}
import itmo.escience.simenv.simulator.events.EventQueue
import org.junit.Test

/**
 * Created by Mishanya on 01.11.2015.
 */
@Test
class HandlerTest {
  // TODO: remake all later
  @Test
  def testRunTask(): Unit = {
//    var ctx: Context = new Context()
//    var eq: EventQueue = new EventQueue()
//    var node: Node = new Node("n_0", 10, new Storage("s_0", 1000), 1)
//    var file: DataFile = new DataFile("f0", 10)
//    var task: DaxTask = new DaxTask("t_0", 30, List(new DataFile("in0", 50)), List(file))
//    ctx.addNode(node)
//    var si: ScheduleItem = new ScheduleItem(node, task, 0, 5, 2)
//    ctx.schedule.addItem(node, si)
//    EventHandler.taskFailer(si, ctx, eq)
//    assert(!node.isFree())
//    assert(node.releaseTime(0) == 5)
//    assert(eq.next().node.name == node.name)
//    assert(ctx.schedule.map(node).isEmpty)
  }

  @Test
  def testFailedTask(): Unit = {
//    var ctx: Context = new Context()
//    var eq: EventQueue = new EventQueue()
//    var node: Node = new Node("n_0", 10, new Storage("s_0", 1000), 0)
//    var file: DataFile = new DataFile("f0", 10)
//    var task: DaxTask = new DaxTask("t_0", 30, List(new DataFile("in0", 50)), List(file))
//    ctx.addNode(node)
//    var si: ScheduleItem = new ScheduleItem(node, task, 0, 5, 2)
//    ctx.schedule.addItem(node, si)
//    EventHandler.taskFailer(si, ctx, eq)
//    assert(node.isFree())
//    assert(eq.next().node.name == node.name)
//    assert(ctx.schedule.map(node).size == 1)
//    assert(si.isFailed)
  }
}