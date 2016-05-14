package itmo.escience.simenv.entities


import scala.collection.mutable

/**
  * Created by Mishanya on 14.01.2016.
  */
class CarrierNodeEnvironment[N <: Node](nodesSeq: List[Node], networksSeq: List[Network]) extends Environment[N] {

  val _nodes: mutable.HashMap[String, Node] = new mutable.HashMap[String, Node]()

  for (x <- nodesSeq){
    _nodes.put(x.id, x)
  }

  var _networks = mutable.HashSet(networksSeq:_*)

  /**
    * Adds physical or virtual nodes to the pool of resources
    * the node is virtual if we cannot directly control physical machine when it runs
    * (for example, a node which has been bought of Amazon EC2 or GAE)
    *
    * @param nodes sequence of nodes
    * @return
    */
  override def addNodes(nodes: List[N]): Unit = {
    for (node <- nodes) {
      if (_nodes.contains(node.id)) {
        throw new IllegalArgumentException(s"Node ${node.id} is already added")
      }
    }

    for (node <- nodes) {
      _nodes.put(node.id, node)
    }
  }

  override def nodeById(nodeId: String): N = {
    if (_nodes.contains(nodeId)) {
      _nodes.get(nodeId).get.asInstanceOf[N]
    } else {
      nodes.filter(x => x.id == nodeId).head
    }
  }

  override def nodes: List[N] = {
    var result: List[N] = List[N]()
    for (key <- _nodes.keySet) {
      val node = _nodes.get(key).get
      node match {
        case carrier: Carrier[N] =>
          result = result ++ carrier.children
        case _ => result :+ node
      }
    }
    result
  }

  def nodesIds: List[String] = nodes.map(x => x.id)

  override def changeNodeParams(newNodeDescription: N): Unit = ???

  override def networksByNode(node: N): List[Network] = {
    networks.filter(x => x.nodes.map(y => y.id).contains(node.parent)) ++ networks.filter(x => x.nodes.map(y => y.id).contains(node.id))
  }

  def removeNodes(nodesIds: List[String]): Unit = {
    for (id <- nodesIds) {
      _nodes.remove(id)
    }
  }

  override def carriers: List[Carrier[N]] = _nodes.map({case (nodeId, node) => node}).
    toList.filter(x => x.isInstanceOf[Carrier[N]]).map(x => x.asInstanceOf[Carrier[N]])

  override def networks: List[Network] = _networks.toList

  def bandwidthBetweenNodes(n1: String, n2: String): Double = {
    val from_networks = networksByNode(nodeById(n1))
    val to_networks = networksByNode(nodeById(n2))

    val transferNetwork = from_networks.intersect(to_networks).max(new Ordering[Network] {
      override def compare(x: Network, y: Network): Int = x.bandwidth.compare(y.bandwidth)
    })
    transferNetwork.bandwidth
  }

  def envPrint(): String = {
    var res: String = ""
    for (n <- nodes) {
      res += s"(${n.asInstanceOf[CpuRamNode].printNode()})\n"
    }
    res
  }

}
