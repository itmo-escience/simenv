package itmo.escience.Environment.Events

/**
 * Created by Mishanya on 14.10.2015.
 */
class EventQueue {
  var eq: List[Event] = List()

  def next(): Event = {
    val e: Event = eq.head
    eq = eq.tail
    return e
  }

  def isEmpty(): Boolean = eq.isEmpty

  // Add new event in order of start time
  def addEvent(event: Event): Unit = {
    var i = 0
    for (i <- eq.indices) {
      if (eq(i).startTime > event.startTime) {
        //TODO check, how to do this right, and test that
        eq = eq.take(i) ++ List(event) ++ eq.takeRight(eq.size - i)
        return
      }
    }
    eq :+= event
  }

  // Check correctness of the order
  def isCorrectOrder(): Boolean = {
    if (eq.size < 2) {
      return true
    }
    var i = 0
    for (i <- 0 until eq.size - 1) {
      if (eq(i).startTime > eq(i+1).startTime) {
        return false
      }
    }
    return true
  }
}
