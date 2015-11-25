package itmo.escience.Environment.ResourceManagers

import itmo.escience.Environment.Entities.Node
import itmo.escience.Environment.ResourceManager

/**
  * Created by Mishanya on 24.11.2015.
  */
class SimpleResourceManager extends ResourceManager {

  var nodes: List[Node] = List()

  def setNodes(nodes: List[Node]): Unit = {
    this.nodes = nodes
  }
}
