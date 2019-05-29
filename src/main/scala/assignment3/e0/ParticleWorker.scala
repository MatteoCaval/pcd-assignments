package assignment3.e0

import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicInteger

object ParticleWorker {
  /* for identifier purposed */ private val workerId = new AtomicInteger(0)
}

class ParticleWorker(world: World, stopFlag: Flag, /* for coordination with the master */ var nextStep: Semaphore, var stepDone: ResettableLatch, /* particles in charge */ var from: Int, val num: Int) extends AbstractBasicAgent("worker-" + ParticleWorker.workerId.incrementAndGet, world, stopFlag) {
  private var to_limit:Int = from + num - 1
  /* for managing new particles */
  private var newParticle = false

  override def run(): Unit = {
    log("Working from " + from + " to " + to_limit)
    val dt = world.getTimestep
    while ({!stopFlag.isSet})
      try {
      log("waiting next step.")
      /* wait for master signal */
      nextStep.acquire()
      /* extension | managing dynamically added bodies */
      checkForNewParticles()
      /* update bodies in charge */
      for(i<-from to to_limit){
        val body = world.getBody(i)
        val force = world.computeForces(i)
        body.update(force, dt)
      }

      log("job done.")
      /* notify completion */
      stepDone.down()
    } catch {
      case ex: Exception =>
      // ex.printStackTrace();
    }
    log("completed.")
  }

  /*
   * it works only for the last worker
   */
  private def checkForNewParticles(): Unit =
    if (newParticle) {
      to_limit = to_limit + 1
      newParticle = false
    }

  def notifyNewParticle(): Unit = newParticle = true
}