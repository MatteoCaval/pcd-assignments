package assignment3.e0

import java.util.concurrent.Semaphore

class ParticleMaster(world: World, stopFlag: Flag, nParticles: Int, nSteps: Int = 0, viewer: WorldViewer = null, buffer: ParticleBuffer = null) extends AbstractBasicAgent("master", world, stopFlag) {
  private var nextSteps:Array[Semaphore] = _
  private var stepDone: ResettableLatch = _
  private var workers:Array[ParticleWorker] = _
  private var nWorkers:Int = 0
  private var newParticles:ParticleBuffer = buffer

  override def run(): Unit = {
    log("init world...")
    world.init(nParticles)
    log("init workers...")
    initWorkers()
    log("starting simulation.  ")
    if (viewer != null) doSimulationWithGUI()
    else doSimulationWithChrono(nSteps)
  }

  private def initWorkers(): Unit = {
    nWorkers = Runtime.getRuntime.availableProcessors + 1
    log("creating workers " + nWorkers)
    workers = new Array[ParticleWorker](nWorkers)
    nextSteps = new Array[Semaphore](nWorkers)
    stepDone = new ResettableLatch(nWorkers)
    val nPartPerWorker = world.getNumParticles / nWorkers
    var nRem = world.getNumParticles % nWorkers
    var from = 0

    for (i <- 0 to nWorkers){
      nextSteps(i) = new Semaphore(0)
      var num = nPartPerWorker
      if (nRem > 0) {
        num += 1
        nRem -= 1
      }
      workers(i) = new ParticleWorker(world, stopFlag, nextSteps(i), stepDone, from, num)
      workers(i).start()
      from = from + num
    }
  }

  private def doSimulationWithGUI(): Unit = {
    world.backupPositions()
    while ( {
      !stopFlag.isSet
    }) {
      stepDone.reset()
      /* notify workers to make a new step */
      for (s <- nextSteps) {
        s.release()
      }

      try {
        /* wait for all workers to complete their job */
        stepDone.await()
        /* check for new particles to add */
        val newPart = newParticles.getNewParticleAvail
        if (newPart != null) {
          world.addParticle(newPart)
          workers(workers.length - 1).notifyNewParticle()
        }
        /* update world */
        world.backupPositions()
        world.pushSnapshotToDisplay()
        world.updateTime()
        /* update view */ viewer.updateView()
        // Thread.sleep(20);
      } catch {
        case ex: Exception =>
          ex.printStackTrace()
      }
    }
    log("completed.")
    for (pw <- workers) {
      pw.interrupt()
    }
  }

  private def doSimulationWithChrono(nSteps: Int): Unit = {
    world.backupPositions()
    val chrono = new Chrono
    chrono.start()
    log("Started.")
    while ( {
      world.getCurrentStep < nSteps
    }) {
      stepDone.reset()
      for (s <- nextSteps) {
        s.release()
      }
      try {
        stepDone.await()
        world.backupPositions()
        world.updateTime()
      } catch {
        case ex: Exception =>
          ex.printStackTrace()
      }
    }
    chrono.stop()
    val dt = chrono.getTime
    val timePerStep = dt.toDouble / nSteps
    log("Done " + nSteps + " steps with " + world.getNumParticles + " particles using " + nWorkers + " workers in: " + dt + "ms")
    log("- " + timePerStep + " ms per step")
    System.exit(0)
  }
}