package itmo.escience.simenv.utilities

import java.io.File
import javax.xml.parsers.{ParserConfigurationException, DocumentBuilderFactory, DocumentBuilder}
import javax.xml.transform.{Transformer, TransformerFactory}
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

import org.w3c.dom.{Element, Document}

import itmo.escience.simenv.utilities.Units._
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

//    val GB = 1024*1024*1024
//    val MB = 1024*1024
//    val KB = 1024



    val BIG_DATA_SIZE = 1 GB
    val MIDDLE_DATA_SIZE = 512 MB
    val SMALL_DATA_SIZE = 50 MB
    val TINY_DATA_SIZE = 1 MB

    val sweeps0 = 8
    val input0 = (doc, "dataflow", MIDDLE_DATA_SIZE)
    val jobs0: List[Element] = genSweepJob(doc, "T000", namespace, "twitterstreaminglistener", 10 Min, input0, "data", SMALL_DATA_SIZE, sweeps0)
    for (j <- jobs0) {
      adag.appendChild(j)
      
    }

    val output1 = genOutput(doc, "dataqueue", SMALL_DATA_SIZE)
    val job1: Element = genJob(doc, "T001", namespace, "Data Queue",  3 Min, "data", sweeps0, SMALL_DATA_SIZE, output1)
    adag.appendChild(job1)
    

    val input2_1 = genInput(doc, "dataqueue", SMALL_DATA_SIZE)
    val output2_1 = genOutput(doc, "userstorage", TINY_DATA_SIZE)
    val job2_1: Element = genJob(doc, "T002_U", namespace, "UserStorage", 2 Min, input2_1, output2_1)
    adag.appendChild(job2_1)
    


    val sweeps2 = 6
    val input2 = (doc, "dataqueue", SMALL_DATA_SIZE)
    val jobs2: List[Element] = genSweepJob(doc, "T002", namespace, "Data Processor", 20 Min, input2, "dataprocessor", SMALL_DATA_SIZE, sweeps2)
    for (j <- jobs2) {
      adag.appendChild(j)
      
    }
    val output3 = genOutput(doc, "leveldetector", MIDDLE_DATA_SIZE)
    val job3: Element = genJob(doc, "T003", namespace, "Change Level Detector", 2 Min, "dataprocessor", sweeps2, SMALL_DATA_SIZE, output3)
    adag.appendChild(job3)


    val sweeps4 = 4
    val input4 = (doc, "leveldetector", MIDDLE_DATA_SIZE)
    val jobs4: List[Element] = genSweepJob(doc, "T004", namespace, "TF-IDF", 12 Min, input4, "tf-idf", TINY_DATA_SIZE, sweeps4)
    for (j <- jobs4) {
      adag.appendChild(j)

    }

    val output5 = genOutput(doc, "clusterization", MIDDLE_DATA_SIZE)
    val job5: Element = genJob(doc, "T005", namespace, "Clusterization", 10 Min, "tf-idf", sweeps4, TINY_DATA_SIZE, output5)
    adag.appendChild(job5)


    val sweeps6 = 2
    val input6 = (doc, "clusterization", MIDDLE_DATA_SIZE)
    val jobs6: List[Element] = genSweepJob(doc, "T006", namespace, "Simulation Modelling", 30 Min, input6, "simulation", SMALL_DATA_SIZE, sweeps6)
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
