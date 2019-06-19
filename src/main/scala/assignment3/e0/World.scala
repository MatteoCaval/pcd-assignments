package assignment3.e0

import java.util._

import scala.collection.mutable.ArrayBuffer

class World(var dt: Double) {
  private var particles: ArrayBuffer[Particle] = ArrayBuffer()
  private var currentPosSnapshot: ArrayBuffer[P2d] = ArrayBuffer()
  private var snapToDisplay: WorldSnapshot = _
  private val kParam: Int = 1
  private var currentStep = 0L

  def init(nParticles: Int): Unit = {
    particles.clear()
    val rand = new Random(System.currentTimeMillis)

    for (i <- 0 to nParticles) {
      val x = rand.nextDouble * 2 - 1
      val y = rand.nextDouble * 2 - 1
      particles += new Particle(new P2d(x, y), new V2d(0, 0), 1, 1, 1)
    }

    currentStep = 0
  }

  def backupPositions(ps: ArrayBuffer[Particle]): Unit = {
    currentPosSnapshot.clear()
    ps.foreach(p => currentPosSnapshot += p.getPos)
  }


  def pushSnapshotToDisplay(): Unit = {
    try {
      snapToDisplay = new WorldSnapshot(this.currentPosSnapshot, this.getCurrentTime)
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
    }
  }

  def addParticle(p: Particle): Unit = particles += p

  def getSnapshotToDisplay: WorldSnapshot = {
    snapToDisplay
  }

  def getNumParticles: Int = particles.size

  def updateTime(): Unit = currentStep += 1

  def getCurrentSteps: Long = currentStep

  def getCurrentTime: Double = currentStep * dt

  def dump(): Unit = {
    System.out synchronized System.out.println(" == WORLD == ")
    particles.foreach(p => System.out.println("Particle pos: " + p.getPos + " vel: " + p.getVel))
    System.out.println(" ========== ")
  }
}