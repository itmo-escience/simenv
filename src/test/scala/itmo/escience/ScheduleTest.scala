package itmo.escience

import itmo.escience.Environment.Context
import itmo.escience.Environment.Entities._
import itmo.escience.Environment.Events.EventQueue
import org.junit.Test

/**
 * Created by Mishanya on 30.10.2015.
 */
@Test
class ScheduleTest {
  @Test
  def testCtxAddNodes(): Unit = {
    var nodes: List[Node] = List()
    nodes :+= new Node("n_0", 10, new Storage("s_0", 1000), 0.8)
    nodes :+= new Node("n_1", 15, new Storage("s_1", 1000), 0.8)
    val ctx: Context = new Context()
    ctx.addNodes(nodes)
    assert(ctx.nodes.size == 2, "Wrong context addNodes")
    ctx.addNode(new Node("n_2", 20, new Storage("s_2", 1000), 0.7))
    assert(ctx.nodes.size == 3, "Wrong context addNode")
    assert(ctx.schedule.map.size == 3, "Wrong context addNode")
    nodes = nodes.take(2)
    assert(ctx.nodes.size == 3, "Wrong context addNode")
  }

  @Test
  def testApplySchedule(): Unit = {
    var tasks: List[Task] = List()
    tasks :+= new Task("t_0", 30, List(new DataFile("in0", 50)), List(new DataFile("f0", 10)))
    tasks :+= new Task("t_1", 10, List(new DataFile("in1", 30)), List(new DataFile("f1", 20)))
    tasks :+= new Task("t_2", 15, List(new DataFile("in2", 40)), List(new DataFile("f2", 15)))
    var nodes: List[Node] = List()
    nodes :+= new Node("n_0", 10, new Storage("s_0", 1000), 1)
    nodes :+= new Node("n_1", 15, new Storage("s_1", 1000), 1)
    val ctx: Context = new Context()
    var sched: Schedule = new Schedule()
    var map: Map[Node, List[ScheduleItem]] = Map()
    var n: Node = null
    for (n <- nodes) {
      map += (n -> List())
    }
    sched.map = map
    sched.addItem(nodes(0), new ScheduleItem(nodes(0), tasks(0), 0, 4, 1))
    sched.addItem(nodes(1), new ScheduleItem(nodes(1), tasks(1), 0, 3, 0))
    sched.addItem(nodes(0), new ScheduleItem(nodes(0), tasks(2), 4, 7, 2))

    assert(sched.map(nodes(0)).size == 2, "Wrong addItem")
    assert(sched.map(nodes(1)).size == 1, "Wrong addItem")
    var eq: EventQueue = new EventQueue()
    ctx.applySchedule(sched, eq)
    assert(ctx.schedule.map(nodes(0)).size == 1, "Wrong apply schedule")
    assert(ctx.schedule.map(nodes(1)).size == 0, "Wrong apply schedule")
    assert(ctx.nodes(0).releaseTime(0) == 4, "Wrong apply schedule")
    assert(ctx.nodes(1).releaseTime(0) == 3, "Wrong apply schedule")
    assert(nodes(1).releaseTime(0) == 3, "Wrong apply schedule")
  }

  @Test
  def testNodeExecuting(): Unit = {
    var node: Node = new Node("n_0", 10, new Storage("s_0", 1000), 1)
    var file0: DataFile = new DataFile("f0", 10)
    var file1: DataFile = new DataFile("f1", 20)
    var task0: Task = new Task("t_0", 30, List(new DataFile("in0", 50)), List(file0))
    var task1: Task = new Task("t_1", 10, List(new DataFile("in1", 30)), List(file1))
    node.runTask(new ScheduleItem(node, task0, 0, 4, 1))
    assert(!node.isFree(), "Wrong node executing")
    assert(node.releaseTime(0) == 4, "Wrong node executing")
    var released: ScheduleItem = node.releaseNode()
    assert(node.releaseTime(5) == 5, "Wrong node executing")
    assert(released.task.name == "t_0")
    assert(node.isFree())
    assert(node.storage.containsFile(file0))
    node.runTask(new ScheduleItem(node, task1, 4, 7, 2))
    assert(!node.isFree())
    assert(node.releaseTime(6) == 7)
    node.releaseNode()
    assert(node.releaseTime(6) == 6)
    assert(node.storage.containsFile(file0))
    assert(node.storage.containsFile(file1))
  }
}
