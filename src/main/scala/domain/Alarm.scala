package domain

trait Alarm[Key, State] {
  def predicate: (Key, State) => Boolean
  def perform: (Key, State) => Unit
}
