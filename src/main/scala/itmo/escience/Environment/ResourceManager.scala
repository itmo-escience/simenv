package itmo.escience.Environment

import itmo.escience.Environment.Entities.Node

/**
  * Created by Mishanya on 24.11.2015.
  */
trait ResourceManager {

  var nodes: List[Node]

  def setNodes(nodes: List[Node])

}
