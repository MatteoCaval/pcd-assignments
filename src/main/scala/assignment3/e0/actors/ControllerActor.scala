package assignment3.e0.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import assignment3.e0.actors.ActorActions._
import assignment3.e0.{Particle, World, WorldViewer}

import scala.collection.mutable.ArrayBuffer

object ControllerActor {
  def props(world: World, worldViewer: WorldViewer) = Props(new ControllerActor(world, worldViewer))
}

class ControllerActor(private val world: World, private val worldViewer: WorldViewer) extends Actor with ActorLogging {

  var particleMaster: ActorRef = _

  override def receive: Receive = {
    case Start(nParticles) =>
      log.info("simulation started")
      particleMaster = context.actorOf(Props[ParticleMasterActor])

      //particles created and added to master
      val particles = ParticleComputationUtils.createNParticles(nParticles)
//      log.info(s"particles created: ${particles.map(p => p.getPos.toString)}")
      particles.foreach(p => particleMaster ! AddParticle(p))

      Thread.sleep(1000)

      particleMaster ! Compute(particles)
      context.become(computation)
  }

  def computation: Receive = {
    case ComputationDone(results) =>
//      log.info("computation done by master")
      updateWorld(results)
      particleMaster ! ComputeNext
  }

  private def updateWorld(results: List[Particle]): Unit = {
    world.backupPositions(ArrayBuffer(results.toArray: _*))
    world.pushSnapshotToDisplay()
    world.updateTime()
    /* update view */
    worldViewer.updateView()
  }

}
