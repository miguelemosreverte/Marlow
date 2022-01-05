package domain

trait Alarm[State] {
  def predicate(key: String, state: State): Boolean
  def perform(key: String, state: State): Unit
}
