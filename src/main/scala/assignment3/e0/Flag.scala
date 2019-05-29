package assignment3.e0

class Flag() {
  private var state:Boolean = false

  def set(): Unit = synchronized{
    state = true
  }

  def isSet: Boolean = synchronized{
    state
  }

  def reset(): Unit = synchronized{
    state = false
  }
}