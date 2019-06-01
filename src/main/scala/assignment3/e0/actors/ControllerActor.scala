package assignment3.e0.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import assignment3.e0.V2d
import assignment3.e0.actors.ActorActions._
import assignment3.e0.{Particle, World, WorldViewer}

import scala.collection.mutable.ArrayBuffer

object ControllerActor {
  def props(world: World, worldViewer: WorldViewer, mode: Mode) = Props(new ControllerActor(world, worldViewer, mode))
}

class ControllerActor(private val world: World, private val worldViewer: WorldViewer, private var mode: Mode) extends Actor with ActorLogging {

  var particleMaster: ActorRef = _

  override def receive: Receive = idle

  def idle: Receive = {
    case Start(nParticles) =>
      log.info("simulation started")
      particleMaster = context.actorOf(ParticleMasterActor.props(self))

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
      if (mode == ContinuousMode) {
        particleMaster ! ComputeNext
      }
    case Stop =>
      log.info("stopping simulation")
      context.stop(particleMaster)
      context.become(idle)
    case AddParticleByPosition(position) =>
      val particle = new Particle(position, new V2d(0, 0), 1, 1, 1)
      particleMaster ! AddParticle(particle)

    case RemoveParticle =>
      particleMaster ! RemoveParticle

    case ContinuousMode => mode = ContinuousMode

    case StepByStepMode => mode = StepByStepMode


  }

  private def updateWorld(results: List[Particle]): Unit = {
    world.backupPositions(ArrayBuffer(results.toArray: _*))
    world.pushSnapshotToDisplay()
    world.updateTime()
    /* update view */
    worldViewer.updateView()
  }


}
