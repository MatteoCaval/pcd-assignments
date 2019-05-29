package assignment3.e0

class Flag() {
  var state:Boolean = false

  def set(): Unit = state = true

  def isSet: Boolean = state

  def reset(): Unit = state = false
}