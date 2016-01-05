package itmo.escience.simenv.environment.entities

import itmo.escience.simenv.common.NameAndId

/**
 * Created by Mishanya on 14.10.2015.
 */

trait Storage extends NameAndId[StorageId] {

}

object NullStorage extends Storage {
  override def id: StorageId = "NULL_STORAGE"

  override def name: String = "NULL_STORAGE"
}

class SimpleStorage (val id: StorageId, val name: String, val volume: Int, val parent: Storage = NullStorage) extends Storage{
  var _files: List[DataFile] = List()

  def addFile(file: DataFile): Unit = {
    //TODO check available space
    _files :+= file
  }

  def files = _files
}
