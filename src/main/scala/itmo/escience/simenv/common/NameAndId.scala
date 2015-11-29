package itmo.escience.simenv.common

/**
 * Created by user on 27.11.2015.
 */
trait NameAndId[T] {
  def id: T
  def name: String
}
