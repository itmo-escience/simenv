package itmo.escience.environment.entities

import itmo.escience.common.NameAndId

/**
 * Created by Mishanya on 29.10.2015.
 */
class DataFile (val id: DataFileId, val name: String, val cVolume: Double) extends NameAndId[DataFileId]
