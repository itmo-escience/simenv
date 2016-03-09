package itmo.escience.simenv.utilities

import java.io._
import java.nio.file.{Paths, Files}
import javax.xml.parsers.{ParserConfigurationException, DocumentBuilderFactory, DocumentBuilder}
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.{TransformerException, Transformer, TransformerFactory}

import itmo.escience.simenv.environment.entities._
import itmo.escience.simenv.environment.entitiesimpl.CarrierNodeEnvironment
import itmo.escience.simenv.environment.modelling.Environment
import net.sf.jedule.JeduleStarter
import org.w3c.dom.{Element, Document}
import org.xml.sax.{InputSource, SAXException}
import sun.reflect.generics.reflectiveObjects.NotImplementedException

/**
  * Created by Mishanya on 29.10.2015.
  */
class ScheduleVisualizer[T <: Task, N <: Node] {
  var counter: Int = 0

  val cmapFilename = "./temp/lastRunSchedules/cmap.xml"

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

  def drawSched(sched: Schedule[T, N], env: CarrierNodeEnvironment[CapacityBasedNode]): Unit = {
    val jed: Document = schedToXML(sched, env)
    source = new DOMSource(jed)
    result = new StreamResult(new File("./temp/lastRunSchedules/jedule.jed"))
    transformer.transform(source, result)

    val jedArgs: Array[String] = new Array[String](12)
    jedArgs(0) = "-p"
    jedArgs(1) = "simgrid"
    jedArgs(2) = "-f"
    jedArgs(3) = "./temp/lastRunSchedules/jedule.jed"
    jedArgs(4) = "-d"
    jedArgs(5) = "1024x720"
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

  def schedToXML(sched: Schedule[T, N], env: CarrierNodeEnvironment[CapacityBasedNode]): Document = {
    // TODO: remake it later
    val doc: Document = db.newDocument
    var nodes = env.nodes
    var carriers = env.carriers

    val grid_schedule: Element = doc.createElement("grid_schedule")
    doc.appendChild(grid_schedule)

    val grid_info: Element = doc.createElement("grid_info")
    grid_schedule.appendChild(grid_info)

    val clusters: Element = doc.createElement("clusters")
    grid_info.appendChild(clusters)

    val carIdx = carriers.zipWithIndex
    for ((carrier, i) <- carIdx) {
      val cluster: Element = doc.createElement("cluster")
      cluster.setAttribute("id", i + "")
      cluster.setAttribute("hosts", "" + carrier.asInstanceOf[CapacityBasedCarrier].capacity.toInt)
      cluster.setAttribute("first_host", "0")
      clusters.appendChild(cluster)
    }

    val node_infos: Element = doc.createElement("node_infos")
    grid_schedule.appendChild(node_infos)


    for ((carrier, i) <- carIdx) {

      val curNodes = nodes.filter(x => x.parent == carrier.asInstanceOf[CapacityBasedCarrier].id)

      var usedNodes = 0

      for (n <- curNodes.indices) {
        val node: CapacityBasedNode = curNodes(n)

        if (sched.getMap.containsKey(node.id)) {
          val nodesSched: List[TaskScheduleItem[T, N]] = sched.getMap.get(node.id).toList.asInstanceOf[List[TaskScheduleItem[T, N]]]

          for (si <- nodesSched) {

            // Runtime
            val node_statistics: Element = doc.createElement("node_statistics")
            node_infos.appendChild(node_statistics)

            val node_id: Element = doc.createElement("node_property")
            node_id.setAttribute("name", "id")
            node_id.setAttribute("value", si.task.id)

            val node_type: Element = doc.createElement("node_property")
            node_type.setAttribute("name", "type")

            var node_fail_time = 0.0

            if (si.status == ScheduleItemStatus.FAILED) {
              node_type.setAttribute("value", "failed")
              node_fail_time = 5.0
            }
            else {
              node_type.setAttribute("value", "waiting")
            }

            val node_startTime: Element = doc.createElement("node_property")
            node_startTime.setAttribute("name", "start_time")
            node_startTime.setAttribute("value", "" + si.startTime)

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
            conf_cluster.setAttribute("value", "" + i)

            val conf_hosts: Element = doc.createElement("conf_property")
            conf_hosts.setAttribute("name", "host_nb")
            conf_hosts.setAttribute("value", "" + si.node.asInstanceOf[CapacityBasedNode].capacity.toInt)

            configuration.appendChild(conf_cluster)
            configuration.appendChild(conf_hosts)

            val host_lists: Element = doc.createElement("host_lists")
            configuration.appendChild(host_lists)

            val hosts: Element = doc.createElement("hosts")
            hosts.setAttribute("start", "" + usedNodes)
            hosts.setAttribute("nb", "" + si.node.asInstanceOf[CapacityBasedNode].capacity.toInt)
            host_lists.appendChild(hosts)


          }
          usedNodes += node.asInstanceOf[CapacityBasedNode].capacity.toInt
        }
      }
    }

    doc
    //    throw new NotImplementedException()
  }

}
