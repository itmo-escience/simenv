package itmo.escience.simenv.utilities

import java.io._
import java.nio.file.{Paths, Files}
import java.util
import java.util.Random
import javax.xml.parsers.{ParserConfigurationException, DocumentBuilderFactory, DocumentBuilder}
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.{TransformerException, Transformer, TransformerFactory}

import itmo.escience.simenv.environment.entities._
import net.sf.jedule.JeduleStarter
import org.w3c.dom.{Element, Document}
import org.xml.sax.{InputSource, SAXException}
import sun.reflect.generics.reflectiveObjects.NotImplementedException

/**
 * Created by Mishanya on 29.10.2015.
 */
class StormScheduleVisualizer(tasks: util.HashMap[TaskId, DaxTask]) {
  val wfNames = workflowNames()
  val wfColors = workflowColors()

  var counter: Int = 0

  val cmapFilename = "./temp/lastRunSchedules/cmap.xml"

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


  val tempDir: File = new File("./temp/lastRunSchedules")
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

  def drawSched (resources: util.HashMap[NodeId, CapacityBandwidthResource]): Unit = {
    val jed: Document = schedToXML(resources)
    source = new DOMSource(jed)
    result = new StreamResult(new File("./temp/lastRunSchedules/jedule.jed"))
    transformer.transform(source, result)

    val jedArgs: Array[String] = new Array[String](12)
    jedArgs(0) = "-p"
    jedArgs(1) = "simgrid"
    jedArgs(2) = "-f"
    jedArgs(3) = "./temp/lastRunSchedules/jedule.jed"
    jedArgs(4) = "-d"
    jedArgs(5) = "10000x512"
    jedArgs(6) = "-o"
    jedArgs(7) = "./temp/lastRunSchedules/schedule" + counter + ".png"
    jedArgs(8) = "-gt"
    jedArgs(9) = "png"
    jedArgs(10) = "-cm"
    jedArgs(11) = "./temp/lastRunSchedules/cmap.xml"

    JeduleStarter.main(jedArgs)
    counter += 1
//    Files.delete(Paths.get(jedArgs(3)))
  }

  def schedToXML(resources: util.HashMap[NodeId, CapacityBandwidthResource]) : Document = {
    val doc: Document = db.newDocument
    var nodes: List[CapacityBandwidthResource] = List[CapacityBandwidthResource]()
    val nodeIter = resources.keySet().iterator()
    while (nodeIter.hasNext) {
      val nId = nodeIter.next()
      nodes :+= resources.get(nId)
    }
    nodes = nodes.sortBy(x => x.name)

    val grid_schedule: Element = doc.createElement("grid_schedule")
    doc.appendChild(grid_schedule)

    val grid_info: Element = doc.createElement("grid_info")
    grid_schedule.appendChild(grid_info)

    val clusters: Element = doc.createElement("clusters")
    grid_info.appendChild(clusters)

    for (n <- nodes) {
      val cluster: Element = doc.createElement("cluster")
      cluster.setAttribute("id", n.id.tail)
      cluster.setAttribute("hosts", n.nominalCapacity.toInt + "")
      cluster.setAttribute("first_host", "0")
      clusters.appendChild(cluster)
    }

    val node_infos: Element = doc.createElement("node_infos")
    grid_schedule.appendChild(node_infos)

    // Runtime
    for (n <- nodes) {
      var hostIdx = 0
      val taskIter = n.taskList.keySet().iterator()
      while (taskIter.hasNext) {
        val tId = taskIter.next()
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
        node_startTime.setAttribute("value", "0")

        val node_endTime: Element = doc.createElement("node_property")
        node_endTime.setAttribute("name", "end_time")
//        node_endTime.setAttribute("value", "" + (task.inputVolume() + task.outputVolume()))
        val endTime = evaluateChannel(task, n)
        node_endTime.setAttribute("value", "" + endTime)

        node_statistics.appendChild(node_id)
        node_statistics.appendChild(node_type)
        node_statistics.appendChild(node_startTime)
        node_statistics.appendChild(node_endTime)

        val configuration: Element = doc.createElement("configuration")
        node_statistics.appendChild(configuration)

        val conf_cluster: Element = doc.createElement("conf_property")
        conf_cluster.setAttribute("name", "cluster_id")
        conf_cluster.setAttribute("value", n.id.tail)

        val conf_hosts: Element = doc.createElement("conf_property")
        conf_hosts.setAttribute("name", "host_nb")
        conf_hosts.setAttribute("value", "" + task.execTime.toInt)

        configuration.appendChild(conf_cluster)
        configuration.appendChild(conf_hosts)

        val host_lists: Element = doc.createElement("host_lists")
        configuration.appendChild(host_lists)

        val hosts: Element = doc.createElement("hosts")
        hosts.setAttribute("start", "" + hostIdx)
        hosts.setAttribute("nb", "" + task.execTime.toInt)
        hostIdx += task.execTime.toInt
        host_lists.appendChild(hosts)
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
  def evaluateChannel(task: DaxTask, node: CapacityBandwidthResource): Double = {
    var transfer = 1.0
    if (task.parents.head.isInstanceOf[HeadDaxTask] ||
      !node.taskList.keySet().contains(task.parents.head.id)) {
      transfer += task.inputVolume()
    }
    if (task.children.isEmpty ||
      !node.taskList.keySet().contains(task.children.head.id)) {
      transfer += task.outputVolume()
    }
    transfer
  }
}
