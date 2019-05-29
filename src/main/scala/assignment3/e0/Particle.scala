package assignment3.e0

class Particle(var pos: P2d, var vel: V2d, var alpha: Double, var mass: Double, var kAttr: Double) {
  def update(force: V2d, dt: Double): Unit = {
    val acc = force.mul(1.0 / mass)
    vel = vel.sum(acc.mul(dt))
    pos = pos.sum(vel.mul(dt))
  }

  def getPos: P2d = pos

  def getVel: V2d = vel

  def getMass: Double = mass

  def getAlpha: Double = alpha

  def getAttrCoeff: Double = kAttr
}