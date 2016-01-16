package itmo.escience.simenv.environment.modelling

import itmo.escience.simenv.environment.entities.{Task, Node, DaxTask}

/**
 * Created by user on 27.11.2015.
 * This entity is a facade for scheduler algorithms to operate with performance models of individuals tasks
 * and workflows (TODO: may be it shouldn't and different kinds of performance models have to be splitted into
 * independent facade-entities)
 */
trait Estimator [T <: Task, N <: Node] {

  def calcTime(task: T, node: N): Double

  def calcTransferTime(from: (T, N), to: (T, N)): Double

}
