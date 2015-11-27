package itmo.escience.environment.entities

import itmo.escience.common.NameAndId

/**
 * Created by Mishanya on 14.10.2015.
 */
class Storage (val id: StorageId, val name: String, val volume: Double) extends NameAndId[StorageId]{
  var _files: List[DataFile] = List()

  def addFile(file: DataFile): Unit = {
    _files :+= file
  }

  def files = _files
}
