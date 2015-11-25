package itmo.escience.Environment

import itmo.escience.Environment.Entities.ScheduleItem
import itmo.escience.Executors.Events.EventQueue

/**
  * Created by Mishanya on 24.11.2015.
  */
trait TaskFailer {
  def taskFailer(item: ScheduleItem, ctx: Context): Unit
}
