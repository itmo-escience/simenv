package itmo.escience.simenv.environment.entities

import scala.collection.mutable


/**
  * Created by Mishanya on 23.12.2015.
  */
class PhysicalCapacityResource(val id: NodeId,
                               val name: String,
                               val nominalCapacity: Double,
                               val bandwidth: Double,
                               var reliability: Double = 1,
                               var parent: NodeId = NullNode.id,
                               var status: NodeStatus = Node.UP,
                               var childrenNodes: mutable.HashMap[NodeId, CapacityBasedNode]) extends Node {

  private var _currentCapacity = nominalCapacity
  private var _currentBandwidth = bandwidth

  def addNode(capacityBasedNode: CapacityBasedNode) = {
    if (capacityBasedNode.nominalCapacity > _currentCapacity) {
      throw new IllegalArgumentException("Not enough free capacity for this node")
    }
    childrenNodes.put(capacityBasedNode.id, capacityBasedNode)
    _currentCapacity -= capacityBasedNode.nominalCapacity

  }

  def removeNode(key: NodeId): CapacityBasedNode = {
    childrenNodes.remove(key).get
  }

  def getMap: mutable.HashMap[NodeId, CapacityBasedNode] = {
    childrenNodes
  }

}
