package itmo.escience.Environment.Entities

/**
 * Created by Mishanya on 14.10.2015.
 */
class Storage (cId: String, cVol: Double) {
  val id = cId
  var volume = cVol
  var files: List[DataFile] = List()

  def writeFile(file: DataFile): Unit = {
    files :+= file
  }

  def containsFile(file: DataFile): Boolean = {
    return files.contains(file)
  }
}
