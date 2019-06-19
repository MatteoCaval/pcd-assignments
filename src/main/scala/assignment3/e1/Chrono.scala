package assignment3.e1

class Chrono() {
  private var running = false
  private var startTime = 0L

  def start(): Unit = {
    running = true
    startTime = System.currentTimeMillis
  }

  def stop(): Chrono = {
    startTime = getTime
    running = false
    return this
  }

  def getTime: Long = {
    if (running) System.currentTimeMillis - startTime
    else startTime
  }
}