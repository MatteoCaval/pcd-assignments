package assignment3.e0

import java.util._
import java.util.concurrent.LinkedBlockingQueue

import scala.collection.mutable.ArrayBuffer

class World(var dt: Double, var displayAllSnapPolicy: Boolean) {
  private var particles:ArrayBuffer[Particle] = ArrayBuffer()
  private var currentPosSnapshot:ArrayBuffer[P2d] = ArrayBuffer()
  private var snapBuffer:LinkedBlockingQueue[WorldSnapshot] = new LinkedBlockingQueue()
  private var snapToDisplay: WorldSnapshot = _
  private var kParam:Int = 1
  private var currentStep = 0L

  def this(dt: Double) {
    this(dt, true)
  }

  def init(nParticles: Int): Unit = {
    particles.clear()
    val rand = new Random(System.currentTimeMillis)

    for(i<-0 to nParticles){
      val x = rand.nextDouble * 2 - 1
      val y = rand.nextDouble * 2 - 1
      particles+= new Particle(new P2d(x, y), new V2d(0, 0), 1, 1, 1)

    }

    currentStep = 0
  }

  def getBody(i: Int): Particle = particles(i)

  def backupPositions(): Unit = {
    currentPosSnapshot.clear()
   for (p <- particles) {
      currentPosSnapshot+= p.getPos
    }
  }

  def pushSnapshotToDisplay(): Unit = try if (displayAllSnapPolicy) snapBuffer.put(new WorldSnapshot(this.currentPosSnapshot, this.getCurrentTime))
  else snapToDisplay = new WorldSnapshot(this.currentPosSnapshot, this.getCurrentTime)
  catch {
    case ex: Exception =>
      ex.printStackTrace()
  }

  def addParticle(p: Particle): Unit = particles += p

  def getSnapshotToDisplay: WorldSnapshot = if (displayAllSnapPolicy) snapBuffer.poll
  else snapToDisplay

  def getNumParticles: Int = particles.size

  def getCurrentPosSnaphot: ArrayBuffer[P2d] = currentPosSnapshot

  def computeForces(indexBody: Int): V2d = {
    var force = new V2d(0, 0)
    val b = particles(indexBody)
    val pos = currentPosSnapshot(indexBody)
    val alpha = b.getAlpha

    for (i<-0 to particles.size){
      if (i != indexBody) {
        val b2 = particles(i)
        val pos2 = currentPosSnapshot(i)
        val ds = pos.sub(pos2)
        val dist = ds.abs
        val invd = 1.0 / (dist * dist * dist)
        val f = ds.mul(kParam).mul(alpha).mul(b2.getAlpha).mul(invd)
        force = force.sum(f)
      }
    }

    force = force.sum(b.getVel.mul(-b.getAttrCoeff))
    force
  }

  def updateTime(): Unit = currentStep += 1

  def getTimestep: Double = dt

  def getCurrentStep: Long = currentStep

  def getCurrentTime: Double = currentStep * dt

  def dump(): Unit = {
    System.out synchronized System.out.println(" == WORLD == ")
    for (p <- particles) {
      System.out.println("Particle pos: " + p.getPos + " vel: " + p.getVel)
    }
    System.out.println(" ========== ")
  }
}