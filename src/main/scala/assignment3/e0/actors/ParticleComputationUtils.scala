package assignment3.e0.actors

import scala.util.Random

object ParticleComputationUtils {

  import assignment3.e0._

  val dt = 0.01
  private val kParam = 1

  def createNParticles(nParticles: Int): Seq[Particle] = {
    val random = new Random(System.currentTimeMillis())
    for (_ <- 1 to nParticles) yield {
      val x = random.nextDouble() * 2 - 1
      val y = random.nextDouble() * 2 - 1
      new Particle(new P2d(x, y), new V2d(0, 0), 1, 1, 1)
    }
  }

  def computeForce(p: Particle, particles: Seq[Particle]): V2d = {
    var force = new V2d(0, 0)
    val alpha: Double = p.getAlpha

    //    println(s"calculating force of particle with pos ${p.getPos}, total particles: ${particles.length}")

    for (i <- particles.indices) {
      val p2 = particles(i)
      if (p.getPos.x != p2.getPos.x || p.getPos.y != p2.getPos.y) {
        val pos2: P2d = p2.getPos
        val ds: V2d = p.getPos sub pos2
        val distance: Double = ds.abs
        val invd: Double = 1.0 / (distance * distance * distance)
        val f: V2d = ds.mul(kParam).mul(alpha).mul(p2.getAlpha).mul(invd)
        force = force.sum(f)
      }
    }

    force = force.sum(p.getVel.mul(-p.getAttrCoeff))
    //    println(s"Force: $force")
    force
  }
}
