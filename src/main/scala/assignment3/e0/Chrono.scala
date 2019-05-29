package assignment3.e0

class Chrono() {
  private var running = false
  private var startTime = 0L

  def start(): Unit = {
    running = true
    startTime = System.currentTimeMillis
  }

  def stop(): Unit = {
    startTime = getTime
    running = false
  }

  def getTime: Long = {
    if (running) System.currentTimeMillis - startTime
    else startTime
  }
}