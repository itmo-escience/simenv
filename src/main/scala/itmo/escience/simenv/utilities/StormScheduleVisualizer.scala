package itmo.escience.simenv.utilities

import java.io._
import java.nio.file.{Paths, Files}
import java.util
import java.util.{Calendar, Random}
import javax.xml.parsers.{ParserConfigurationException, DocumentBuilderFactory, DocumentBuilder}
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.{TransformerException, Transformer, TransformerFactory}

import itmo.escience.simenv.entities._
import itmo.escience.simenv.ga.{StormSchedulingProblem, SSSolution}
import net.sf.jedule.JeduleStarter
import org.w3c.dom.{Element, Document}
import org.xml.sax.{InputSource, SAXException}
import sun.reflect.generics.reflectiveObjects.NotImplementedException

/**
 * Created by Mishanya on 29.10.2015.
 */
class StormScheduleVisualizer(env: CarrierNodeEnvironment[CpuRamNode], tasks: util.HashMap[String, DaxTask]) {
  val wfNames = workflowNames()
  val wfColors = workflowColors()

  var counter: Int = 0
  val tempDir: File = new File("./temp/lastRunSchedules/" + java.time.LocalDate.now + "_" + java.time.LocalTime.now.toString.replace(":", ""))
  val cmapFilename = tempDir + "/cmap.xml"

  var cmapString: String =
    "<cmap name=\"default\">" +
      "<conf name=\"min_font_size_label\" value=\"20\" />" +
      "<conf name=\"font_size_label\" value=\"18\" />" +
      "<conf name=\"font_size_axes\" value=\"18\" />"
  for (wf <- wfNames) {
    cmapString += "<task id=\"" + wf + "\">" +
      "<color type=\"fg\" rgb=\"FFFFFF\" />" +
      "<color type=\"bg\" rgb=\"" + wfColors.get(wf).toUpperCase + "\" />" +
      "</task>"
  }

  cmapString += "</cmap>"


  if (tempDir.exists) {
    var f: File = null
    for (f <- tempDir.listFiles()) {
      f.delete()
    }
  }
  if (!tempDir.exists && !tempDir.mkdirs()) {
    throw new Exception(s"Cannot create file: ${tempDir.getCanonicalPath}")
  }

  val cmapWriter = new PrintWriter(new File(cmapFilename))
  cmapWriter.write(cmapString)
  cmapWriter.close()

  var db: DocumentBuilder = null
  try {
    db = DocumentBuilderFactory.newInstance.newDocumentBuilder
  }
  catch {
    case e: ParserConfigurationException => {
      e.printStackTrace
    }
  }

  val is: InputSource = new InputSource

  is.setCharacterStream(new StringReader(cmapString))
  var cmap: Document = null
  try {
    assert(db != null)
    cmap = db.parse(is)
  }
  catch {
    case e: Any => {
      e.printStackTrace()
    }
  }
  val transformerFactory: TransformerFactory = TransformerFactory.newInstance
  val transformer: Transformer = transformerFactory.newTransformer
  var source: DOMSource = new DOMSource(cmap)
  var result: StreamResult = new StreamResult(new File(cmapFilename))
  transformer.transform(source, result)

  def drawSched (solution: SSSolution): Unit = {
    val jed: Document = schedToXML(solution)
    source = new DOMSource(jed)
    result = new StreamResult(new File(tempDir + "/jedule.jed"))
    transformer.transform(source, result)

    val jedArgs: Array[String] = new Array[String](12)
    jedArgs(0) = "-p"
    jedArgs(1) = "simgrid"
    jedArgs(2) = "-f"
    jedArgs(3) = tempDir + "/jedule.jed"
    jedArgs(4) = "-d"
    jedArgs(5) = "10000x512"
    jedArgs(6) = "-o"
    jedArgs(7) = tempDir + "/schedule" + counter + ".png"
    jedArgs(8) = "-gt"
    jedArgs(9) = "png"
    jedArgs(10) = "-cm"
    jedArgs(11) = tempDir + "/cmap.xml"

    JeduleStarter.main(jedArgs)
    counter += 1
//    Files.delete(Paths.get(jedArgs(3)))
  }

//  def drawSched (nodeStr: String, bandwidth: Double, workStr: String, schedule: String) = {
//    // Мапа для содержания нодов по id
//    var nodes: util.HashMap[NodeId, CapRamBandResource] = new util.HashMap[NodeId, CapRamBandResource]
//    // Мапа для хранения тасок по id
//    var tasks: util.HashMap[TaskId, DaxTask] = new util.HashMap[TaskId, DaxTask]
//    // Расписание (На ноде располагаются таски)
//    var schedule: util.HashMap[NodeId, List[TaskId]] = new util.HashMap[NodeId, List[TaskId]]
//
//    var nodesList =  JSONParser.parseEnv(nodeStr, bandwidth)
//    val tasksList = JSONParser.parseWorkload(workStr)
//
//    for (n <- nodesList) {
//      nodes.put(n.id, n)
//    }
//    for (t <- tasksList) {
//      tasks.put(t.id, t)
//    }
//
//
//  }

  def schedToXML(solution: SSSolution) : Document = {
    val schedule = StormSchedulingProblem.solutionToSchedule(solution)
    val doc: Document = db.newDocument
    val nodes = env.nodes

    val nodeMask = new util.HashMap[String, String]()
    var nid = 0
    for (node <- nodes) {
      nodeMask.put(node.id, nid + "")
      nid += 1
    }

    val grid_schedule: Element = doc.createElement("grid_schedule")
    doc.appendChild(grid_schedule)

    val grid_info: Element = doc.createElement("grid_info")
    grid_schedule.appendChild(grid_info)

    val clusters: Element = doc.createElement("clusters")
    grid_info.appendChild(clusters)

    for (n <- nodes) {
      val cluster: Element = doc.createElement("cluster")
//      cluster.setAttribute("id", n.id.tail)
      cluster.setAttribute("id", nodeMask.get(n.id))
      cluster.setAttribute("hosts", n.cpu.toInt + "")
      cluster.setAttribute("first_host", "0")
      clusters.appendChild(cluster)
    }

    val node_infos: Element = doc.createElement("node_infos")
    grid_schedule.appendChild(node_infos)

    // Runtime
    for (n <- nodes) {
      var hostIdx = 0
      var time: Double = 0.0
      if (schedule.containsKey(n.id)) {
        for (item <- schedule.get(n.id)) {

          val tId = item._1
          val proc = item._2
          val task = tasks.get(tId)

          val node_statistics: Element = doc.createElement("node_statistics")
          node_infos.appendChild(node_statistics)

          val node_id: Element = doc.createElement("node_property")
          node_id.setAttribute("name", "id")
          node_id.setAttribute("value", tId)

          val node_type: Element = doc.createElement("node_property")
          node_type.setAttribute("name", "type")
          node_type.setAttribute("value", task.name)

          val node_startTime: Element = doc.createElement("node_property")
          node_startTime.setAttribute("name", "start_time")
          node_startTime.setAttribute("value", "" + time)


          val node_endTime: Element = doc.createElement("node_property")
          node_endTime.setAttribute("name", "end_time")
          //        node_endTime.setAttribute("value", "" + (task.inputVolume() + task.outputVolume()))
          //        node_endTime.setAttribute("value", "" + endTime)
          node_endTime.setAttribute("value", "" + (time + (n.ram * proc).toInt))
          time += (n.ram * proc).toInt

          node_statistics.appendChild(node_id)
          node_statistics.appendChild(node_type)
          node_statistics.appendChild(node_startTime)
          node_statistics.appendChild(node_endTime)

          val configuration: Element = doc.createElement("configuration")
          node_statistics.appendChild(configuration)

          val conf_cluster: Element = doc.createElement("conf_property")
          conf_cluster.setAttribute("name", "cluster_id")
          //        conf_cluster.setAttribute("value", n.id.tail)
          conf_cluster.setAttribute("value", nodeMask.get(n.id))

          val conf_hosts: Element = doc.createElement("conf_property")
          conf_hosts.setAttribute("name", "host_nb")
          conf_hosts.setAttribute("value", "" + (n.cpu * proc).toInt)

          configuration.appendChild(conf_cluster)
          configuration.appendChild(conf_hosts)

          val host_lists: Element = doc.createElement("host_lists")
          configuration.appendChild(host_lists)

          val hosts: Element = doc.createElement("hosts")
          hosts.setAttribute("start", "" + hostIdx)
          hosts.setAttribute("nb", "" + (n.cpu * proc).toInt)
          hostIdx += (n.cpu * proc).toInt
          host_lists.appendChild(hosts)
        }
      }
    }
    doc
  }

  def workflowNames(): List[String] = {
    var result: List[String] = List[String]()
    val taskIter = tasks.keySet().iterator()
    while (taskIter.hasNext) {
      val taskId = taskIter.next()
      val task = tasks.get(taskId)
      if (!result.contains(task.name)) {
        result :+= task.name
      }
    }
    result
  }

  def workflowColors(): util.HashMap[String, String] = {
    val rand = new Random()
    val colors = new util.HashMap[String, String]()
    for (wf <- wfNames) {
      colors.put(wf, Integer.toHexString(rand.nextInt(0xFFFFFF) / 2 + 0x444444))
    }
    colors
  }
//  def evaluateChannel(task: DaxTask, node: CapRamBandResource): Double = {
//    var transfer = 1.0
//    if (task.parents.isEmpty ||
//      !node.taskList.keySet().contains(task.parents.head.id)) {
//      transfer += task.inputVolume()
//    }
//    if (task.children.isEmpty ||
//      !node.taskList.keySet().contains(task.children.head.id)) {
//      transfer += task.outputVolume()
//    }
//    transfer
//  }


}
