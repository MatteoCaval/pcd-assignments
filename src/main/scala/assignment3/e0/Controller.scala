package assignment3.e0

class Controller(var world: World, var viewer: WorldViewer) {
  private var master: ParticleMaster = _
  private var stopFlag: Flag = _
  private var newParticles: ParticleBuffer = new ParticleBuffer

  def notifyStarted(nParticles: Int): Unit = {
    stopFlag = new Flag
    master = new ParticleMaster(world, stopFlag, nParticles, None, viewer, newParticles)
    master.start()
  }

  def notifyStopped(): Unit = stopFlag.set()

  def notifyNewParticle(pos: P2d): Unit =
  /* adding a particle with 100 times the mass and 10 times the charge */
    newParticles.notifyNewParticle(new Particle(pos, new V2d(0, 10), 1000, 10, 1))
}