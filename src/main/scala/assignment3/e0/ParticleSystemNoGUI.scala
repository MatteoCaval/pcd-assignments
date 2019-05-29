package assignment3.e0

/**
  * PCD 2018-2019 - Assignment #01
  *
  * Particle system version for profiling purposes (no GUI)
  *
  * @author aricci
  *
  */
object ParticleSystemNoGUI {
  def main(args: Array[String]): Unit = {
    val nParticles = 1000
    val nSteps:Int = 1000
    val dt = 0.01
    val world = new World(dt)
    val stopFlag = new Flag
    val master = new ParticleMaster(world, stopFlag, nParticles, Some(nSteps))
    master.start()
  }
}