package itmo.escience.simenv.utilities

import java.io.File
import javax.xml.parsers.{ParserConfigurationException, DocumentBuilderFactory, DocumentBuilder}
import javax.xml.transform.{Transformer, TransformerFactory}
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

import org.w3c.dom.{Element, Document}

/**
  * Created by Mishanya on 15.01.2016.
  */
object CrawlerWFGenerator {

    def main(args: Array[String]): Unit = {
      generateCrawlerWf()
    }

  def generateCrawlerWf(): Unit = {
    var db: DocumentBuilder = null
    try {
      db = DocumentBuilderFactory.newInstance.newDocumentBuilder
    }
    catch {
      case e: ParserConfigurationException => {
        e.printStackTrace
      }
    }

    val doc: Document = db.newDocument
    val adag: Element = doc.createElement("adag")
    //
    val namespace = "crawler"


    val sweeps0 = 8
    val input0 = (doc, "dataflow", 1024000)
    val jobs0: List[Element] = genSweepJob(doc, "T000", namespace, "Data Getter", 200, input0, "data", 102400, sweeps0)
    for (j <- jobs0) {
      adag.appendChild(j)
      
    }

    val output1 = genOutput(doc, "dataqueue", 102400)
    val job1: Element = genJob(doc, "T001", namespace, "Data Queue", 300, "data", sweeps0, 102400, output1)
    adag.appendChild(job1)
    

    val input2_1 = genInput(doc, "dataqueue", 102400)
    val output2_1 = genOutput(doc, "userstorage", 10240)
    val job2_1: Element = genJob(doc, "T002_U", namespace, "UserStorage", 100, input2_1, output2_1)
    adag.appendChild(job2_1)
    


    val sweeps2 = 6
    val input2 = (doc, "dataqueue", 102400)
    val jobs2: List[Element] = genSweepJob(doc, "T002", namespace, "Data Processor", 500, input2, "dataprocessor", 102400, sweeps2)
    for (j <- jobs2) {
      adag.appendChild(j)
      
    }
    val output3 = genOutput(doc, "leveldetector", 1024000)
    val job3: Element = genJob(doc, "T003", namespace, "Change Level Detector", 100, "dataprocessor", sweeps2, 102400, output3)
    adag.appendChild(job3)


    val sweeps4 = 4
    val input4 = (doc, "leveldetector", 1024000)
    val jobs4: List[Element] = genSweepJob(doc, "T004", namespace, "TF-IDF", 200, input4, "tf-idf", 10240, sweeps4)
    for (j <- jobs4) {
      adag.appendChild(j)

    }

    val output5 = genOutput(doc, "clusterization", 10240000)
    val job5: Element = genJob(doc, "T005", namespace, "Clusterization", 300, "tf-idf", sweeps4, 10240, output5)
    adag.appendChild(job5)


    val sweeps6 = 2
    val input6 = (doc, "clusterization", 10240000)
    val jobs6: List[Element] = genSweepJob(doc, "T006", namespace, "Simulation Modelling", 1000, input6, "simulation", 102400, sweeps6)
    for (j <- jobs6) {
      adag.appendChild(j)

    }

    adag.appendChild(genChild(doc, "T001", "T000", sweeps0))
    

    for (i <- 0 until sweeps2) {
      adag.appendChild(genChild(doc, "T002_" + i, "T001"))

    }
    adag.appendChild(genChild(doc, "T002_U", "T001"))


    adag.appendChild(genChild(doc, "T003", "T002", sweeps2))


    for (i <- 0 until sweeps4) {
      adag.appendChild(genChild(doc, "T004_" + i, "T003"))

    }
    adag.appendChild(genChild(doc, "T005", "T004", sweeps4))


    for (i <- 0 until sweeps6) {
      adag.appendChild(genChild(doc, "T006_" + i, "T005"))
    }

    //
    doc.appendChild(adag)
    


    val transformerFactory: TransformerFactory = TransformerFactory.newInstance
    val transformer: Transformer = transformerFactory.newTransformer
    var result: StreamResult = new StreamResult(new File("./resources/crawlerWf.xml"))
    var source = new DOMSource(doc)
    transformer.transform(source, result)

  }

  def genJob(doc: Document, id: String, namespace: String, name: String, runtime: Int, input: Element, output: Element) : Element = {

    val job: Element = doc.createElement("job")
    job.setAttribute("id", id)
    job.setAttribute("namespace", namespace)
    job.setAttribute("name", name)
    job.setAttribute("version", "1.0")
    job.setAttribute("runtime", runtime + "")

    job.appendChild(output)
    job.appendChild(input)

    job

  }

  def genJob(doc: Document, id: String, namespace: String, name: String, runtime: Int, inputName: String, inputSweeps: Int, inputSize: Int, output: Element) : Element = {

    val job: Element = doc.createElement("job")
    job.setAttribute("id", id)
    job.setAttribute("namespace", namespace)
    job.setAttribute("name", name)
    job.setAttribute("version", "1.0")
    job.setAttribute("runtime", runtime + "")

    for (i <- 0 until inputSweeps) {
      job.appendChild(genInput(doc, inputName + i, inputSize))
    }
    job.appendChild(output)

    job

  }

  def genSweepJob(doc: Document, id: String, namespace: String, name: String, runtime: Int,
                  input: (Document, String, Int),
                  outputName: String, outputSize: Int, sweeps: Int): List[Element] = {
    var res = List[Element]()
    for (i <- 0 until sweeps) {

      val output = genOutput(doc, outputName + i, outputSize)
      val job = genJob(doc, id + "_" + i, namespace, name + "_" + i, runtime, genInput(input._1, input._2, input._3), output)
      res :+= job
    }
    res
  }

  def genChild(doc: Document, ch: String, par: String): Element = {
    val child = doc.createElement("child")
    child.setAttribute("ref", ch)
    val parent = doc.createElement("parent")
    parent.setAttribute("ref", par)
    child.appendChild(parent)
    child
  }

  def genChild(doc: Document, ch: String, par: String, sweeps: Int): Element = {
    val child = doc.createElement("child")
    child.setAttribute("ref", ch)
    for (i <- 0 until sweeps) {
      val parent = doc.createElement("parent")
      parent.setAttribute("ref", par + "_" + i)
      child.appendChild(parent)
    }

    child
  }

  def genInput(doc: Document, name:String, size: Int): Element = {
    val input: Element = doc.createElement("uses")
    input.setAttribute("file", name)
    input.setAttribute("link", "input")
    input.setAttribute("register", "true")
    input.setAttribute("transfer", "true")
    input.setAttribute("optional", "false")
    input.setAttribute("type", "data")
    input.setAttribute("size", size + "")

    input
  }

  def genOutput(doc: Document, name:String, size: Int): Element = {
    val output: Element = doc.createElement("uses")
    output.setAttribute("file", name)
    output.setAttribute("link", "output")
    output.setAttribute("register", "true")
    output.setAttribute("transfer", "true")
    output.setAttribute("optional", "false")
    output.setAttribute("type", "data")
    output.setAttribute("size", size + "")

    output
  }

}
