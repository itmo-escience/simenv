package itmo.escience.Utilities

import java.io.{StringReader, File, IOException}
import java.nio.file.{Paths, Files}
import javax.xml.parsers.{ParserConfigurationException, DocumentBuilderFactory, DocumentBuilder}
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.{TransformerException, Transformer, TransformerFactory}

import itmo.escience.Environment.Entities.{ScheduleItem, Node, Schedule}
import net.sf.jedule.JeduleStarter
import org.w3c.dom.{Element, Document}
import org.xml.sax.{InputSource, SAXException}

/**
 * Created by Mishanya on 29.10.2015.
 */
class ScheduleVisualizer {
  var counter: Int = 0

  val cmapString: String =
    "<cmap name=\"default\">" +
      "<conf name=\"min_font_size_label\" value=\"14\" />" +
      "<conf name=\"font_size_label\" value=\"18\" />" +
      "<conf name=\"font_size_axes\" value=\"18\" />" +
      "<task id=\"waiting\">" +
        "<color type=\"fg\" rgb=\"FFFFFF\" />" +
        "<color type=\"bg\" rgb=\"0000FF\" />" +
      "</task>" +
      "<task id=\"executing\">" +
        "<color type=\"fg\" rgb=\"FFFFFF\" />" +
        "<color type=\"bg\" rgb=\"00FF00\" />" +
      "</task>" +
      "<task id=\"failed\">" +
        "<color type=\"fg\" rgb=\"FFFFFF\" />" +
        "<color type=\"bg\" rgb=\"FF0000\" />" +
      "</task>" +
      "<task id=\"transfer\">" +
        "<color type=\"fg\" rgb=\"FFFFFF\" />" +
        "<color type=\"bg\" rgb=\"666666\" />" +
      "</task>" +
    "</cmap>"


  val tempDir: File = new File("./temp/lastRunSchedules")
  if (tempDir.exists) {
    var f: File = null
    for (f <- tempDir.listFiles()) {
      f.delete()
    }
  }
  if (!tempDir.exists) {
    tempDir.mkdir
  }

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
  var result: StreamResult = new StreamResult(new File("./temp/lastRunSchedules/cmap.xml"))
  transformer.transform(source, result)

  def drawSched (sched: Schedule): Unit = {
    val jed: Document = schedToXML(sched)
    source = new DOMSource(jed)
    result = new StreamResult(new File("./temp/lastRunSchedules/jedule.jed"))
    transformer.transform(source, result)

    val jedArgs: Array[String] = new Array[String](12)
    jedArgs(0) = "-p"
    jedArgs(1) = "simgrid"
    jedArgs(2) = "-f"
    jedArgs(3) = "./temp/lastRunSchedules/jedule.jed"
    jedArgs(4) = "-d"
    jedArgs(5) = "320x480"
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

  def schedToXML(sched: Schedule) : Document = {
    val doc: Document = db.newDocument
    var nodes: List[Node] = List()
    sched.map.keySet.foreach(x => nodes :+= x)
    nodes = nodes.sortBy(x => x.name)

    val grid_schedule: Element = doc.createElement("grid_schedule")
    doc.appendChild(grid_schedule)

    val grid_info: Element = doc.createElement("grid_info")
    grid_schedule.appendChild(grid_info)

    val clusters: Element = doc.createElement("clusters")
    grid_info.appendChild(clusters)

    val cluster: Element = doc.createElement("cluster")
    cluster.setAttribute("id", "0")
    cluster.setAttribute("hosts", "" + nodes.size)
    cluster.setAttribute("first_host", "0")
    clusters.appendChild(cluster)

    val node_infos: Element = doc.createElement("node_infos")
    grid_schedule.appendChild(node_infos)

    var node: Node = null
    var n: Int = 0
    for (n <- nodes.indices) {
      val node: Node = nodes(n)
      var nodesSched: List[ScheduleItem] = sched.map(node)
      if (!node.isFree()) {
        nodesSched :+= node.executedItem
      }
      var si: ScheduleItem = null
      for (si <- nodesSched) {

        // Runtime
        val node_statistics: Element = doc.createElement("node_statistics")
        node_infos.appendChild(node_statistics)

        val node_id: Element = doc.createElement("node_property")
        node_id.setAttribute("name", "id")
        node_id.setAttribute("value", si.task.name)

        val node_type: Element = doc.createElement("node_property")
        node_type.setAttribute("name", "type")
        if (si == node.executedItem) {
          node_type.setAttribute("value", "executing")
        }
        else {
          if (si.isFailed) {
            node_type.setAttribute("value", "failed")
          }
          else {
            node_type.setAttribute("value", "waiting")
          }
        }

        val node_startTime: Element = doc.createElement("node_property")
        node_startTime.setAttribute("name", "start_time")
        node_startTime.setAttribute("value", "" + (si.startTime + si.transferTime))

        val node_endTime: Element = doc.createElement("node_property")
        node_endTime.setAttribute("name", "end_time")
        node_endTime.setAttribute("value", "" + si.endTime)

        node_statistics.appendChild(node_id)
        node_statistics.appendChild(node_type)
        node_statistics.appendChild(node_startTime)
        node_statistics.appendChild(node_endTime)

        val configuration: Element = doc.createElement("configuration")
        node_statistics.appendChild(configuration)

        val conf_cluster: Element = doc.createElement("conf_property")
        conf_cluster.setAttribute("name", "cluster_id")
        conf_cluster.setAttribute("value", "0")

        val conf_hosts: Element = doc.createElement("conf_property")
        conf_hosts.setAttribute("name", "host_nb")
        conf_hosts.setAttribute("value", "1")

        configuration.appendChild(conf_cluster)
        configuration.appendChild(conf_hosts)

        val host_lists: Element = doc.createElement("host_lists")
        configuration.appendChild(host_lists)

        val hosts: Element = doc.createElement("hosts")
        hosts.setAttribute("start", "" + n)
        hosts.setAttribute("nb", "1")
        host_lists.appendChild(hosts)

//         Transfer
        if (si.transferTime > 0) {
//        if (false) {
          val data_node_statistics: Element = doc.createElement("node_statistics")
          node_infos.appendChild(data_node_statistics)

          val data_node_id: Element = doc.createElement("node_property")
          data_node_id.setAttribute("name", "id")
          data_node_id.setAttribute("value", si.task.name + "_d")

          val data_node_type: Element = doc.createElement("node_property")
          data_node_type.setAttribute("name", "type")
          data_node_type.setAttribute("value", "transfer")

          val data_node_startTime: Element = doc.createElement("node_property")
          data_node_startTime.setAttribute("name", "start_time")
          data_node_startTime.setAttribute("value", "" + si.startTime)

          val data_node_endTime: Element = doc.createElement("node_property")
          data_node_endTime.setAttribute("name", "end_time")
          data_node_endTime.setAttribute("value", "" + (si.startTime + si.transferTime))

          data_node_statistics.appendChild(data_node_id)
          data_node_statistics.appendChild(data_node_type)
          data_node_statistics.appendChild(data_node_startTime)
          data_node_statistics.appendChild(data_node_endTime)

          val data_configuration: Element = doc.createElement("configuration")
          data_node_statistics.appendChild(data_configuration)

          val data_conf_cluster: Element = doc.createElement("conf_property")
          data_conf_cluster.setAttribute("name", "cluster_id")
          data_conf_cluster.setAttribute("value", "0")

          val data_conf_hosts: Element = doc.createElement("conf_property")
          data_conf_hosts.setAttribute("name", "host_nb")
          data_conf_hosts.setAttribute("value", "1")

          data_configuration.appendChild(data_conf_cluster)
          data_configuration.appendChild(data_conf_hosts)

          val data_host_lists: Element = doc.createElement("host_lists")
          data_configuration.appendChild(data_host_lists)

          val data_hosts: Element = doc.createElement("hosts")
          data_hosts.setAttribute("start", "" + n)
          data_hosts.setAttribute("nb", "1")
          data_host_lists.appendChild(data_hosts)
        }
      }
    }

    return doc
  }

}
