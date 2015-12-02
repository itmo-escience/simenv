import java.io.File

import itmo.escience.simenv.environment.entities._

import scala.xml.{Node, XML}
val path = "D:\\wspace\\simenv\\resources\\wf-examples\\Montage_25.xml"
val file = new File(path)
val dax = XML.loadFile(file)
val jobs = dax \ "job"
var childs = dax \ "child"

val idf = (job:Node) => job.attribute("id").get.head.text
val name = (job:Node) => job.attribute("name").get.head.text
val runtime = (job:Node) => job.attribute("runtime").get.head.text


//var childMap = childs.map(x => idf(x) -> x).toMap
//var tasksMap = Map[String, DaxTask]()
//var jbs = jobs.toBuffer
//
//val isTopLevel = (x:Node) => !childMap.contains(idf(x)) || (childMap(idf(x)) \ "parent").forall(p => tasksMap.contains(idf(p)))
//val toTask = (job:Node) => new DaxTask(id=idf(job),
//  name=name(job),
//  execTime=runtime(job),
//  inputData=.,
//val outputData: List[DataFile] = List(),
//val parents: List[DaxTask] = ,
//val children: List[DaxTask])
//
//var tasks = List[DaxTask]()
//
//while(jbs.nonEmpty){
//  val newTopLevelJobs = jbs.filter(x => isTopLevel(x))
//  jbs --= newTopLevelJobs
//  tasksMap ++= newTopLevelJobs.map(x => idf(x) -> toTask(x))
//}



//var jbs = jobs.toBuffer
//
//while (jbs.nonEmpty) {
//  val job = jbs.head
//  jbs -= job
//
//  val id = job.attribute("id").get
//  if (!childMap.contains(id) && !tasksMap.contains(id) ){
//
//  }
//}

