package itmo.escience.simenv.utilities

import java.io.File

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.modelling.Environment
import itmo.escience.simenv.simulator.events.Event
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.{LogManager, Logger, Marker, MarkerManager}

/**
  * Created by Mishanya on 19.01.2016.
  */
object SimLogger {
  val logger: Logger = LogManager.getLogger()
  val logContext: LoggerContext = LogManager.getContext(false).asInstanceOf[org.apache.logging.log4j.core.LoggerContext]
  val file: File = new File("./resources/log4j2.xml")
  logContext.setConfigLocation(file.toURI)

  println("Logger has been initialized")

  val info: Marker = MarkerManager.getMarker("info")
  val event: Marker = MarkerManager.getMarker("event")
  val schedule: Marker = MarkerManager.getMarker("schedule")
  val task: Marker = MarkerManager.getMarker("task")
  val env: Marker = MarkerManager.getMarker("environment")
  val node: Marker = MarkerManager.getMarker("node")

  var _ctx: Context[DaxTask, Node] = null

  def setCtx(ctx: Context[DaxTask, Node]) = {
    _ctx = ctx
  }

  def logEvent(log: Event) = {
    logger.trace(event, s"time: ${_ctx.currentTime}; Event - ${log.getClass.getSimpleName} ${log.name} ${log.eventTime}")
  }

  def logSched(log: Schedule[Task, Node]) = {
    logger.trace(schedule, s"time: ${_ctx.currentTime}; Schedule - \n${log.prettyPrint()}")
  }

  def logTask(log: Task) = {
    logger.trace(task, s"time: ${_ctx.currentTime}; Task - ${log.id}")
  }

  def logEnv(log: Environment[Node]) = {
    logger.trace(env, s"time: ${_ctx.currentTime}; Environment - \n${log.envPrint()}")
  }

  def logNode(log: Node) = {
    logger.trace(node, s"time: ${_ctx.currentTime}; Node - ${log.id} ${log.name}")
  }

  def log(log: String) = {
    logger.trace(info, s"time: ${_ctx.currentTime}; $log")
  }
}
