package itmo.escience.simenv.simulator.events

import itmo.escience.simenv.environment.entities.ModellingTimestamp

import scala.collection.mutable

/**
 * Created by Mishanya on 14.10.2015.
 */
class EventQueue {
  var _currentTime: ModellingTimestamp = 0
  var eq: mutable.PriorityQueue[Event] = new mutable.PriorityQueue[Event]()(new Ordering[Event] {
    override def compare(x: Event, y: Event): Int = -x.eventTime.compare(y.eventTime)
  })

  def next(): Event = {
    val event = eq.dequeue()
    _currentTime = event.eventTime
    event
  }

  def isEmpty: Boolean = eq.isEmpty

  // Add new event in order of start time
  def submitEvent(event: Event): Unit = {

    if (event.eventTime < _currentTime) {
      throw new IllegalArgumentException(s"time of submitted event (${event.eventTime}})" +
        s" is earlier than current time (${_currentTime}})")
    }

    eq += event
  }

  def print(): String = {
    var result = ""
    for (item <- eq) {
      result += s"class ${item.getClass.getName}; post ${item.postTime}; eventTime; ${item.eventTime}\n"
    }
    return result
  }

}
