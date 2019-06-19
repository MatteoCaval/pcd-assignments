package assignment3.e1.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import assignment3.e1.{Chrono, Particle, V2d, World, WorldViewer}
import assignment3.e1.actors.ActorActions._

import scala.collection.mutable.ArrayBuffer

object ControllerActor {
  def props(world: World, worldViewer: WorldViewer, mode: Mode) = Props(new ControllerActor(world, worldViewer, mode))

  def props(world: World, nSteps: Int) = Props(new ControllerActor(world, null, ContinuousMode, nSteps))

}

class ControllerActor(private val world: World, private val worldViewer: WorldViewer, private var mode: Mode,
                      private var nSteps: Int = Integer.MAX_VALUE) extends Actor with ActorLogging {

  var particleMaster: ActorRef = _

  private var stepsDone: Int = 0
  private var chrono: Chrono = _


  override def receive: Receive = idle

  def idle: Receive = {
    case Start(nParticles) =>
      log.info("simulation started")
      particleMaster = context.actorOf(ParticleMasterActor.props(self), "ParticleMaster")

      //particles created and added to master
      val particles = ParticleComputationUtils.createNParticles(nParticles)
      //      log.info(s"particles created: ${particles.map(p => p.getPos.toString)}")

      particleMaster ! AddParticles(particles)

      particleMaster ! Compute(particles)
      chrono = new Chrono
      chrono.start()
      context.become(computation)

    case ContinuousMode =>
      log.info("mode changed to continuous")
      mode = ContinuousMode

    case StepByStepMode =>
      log.info("mode changed to step by step")
      mode = StepByStepMode

    case message =>
      log.info(s"received unhandled message: $message")
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

    case ContinuousMode =>
      log.info("mode changed to continuous")
      mode = ContinuousMode
      particleMaster ! ComputeNext

    case StepByStepMode =>
      log.info("mode changed to step by step")
      mode = StepByStepMode

    case ComputeNext =>
      log.info("next computation")
      particleMaster ! ComputeNext

    case message =>
      log.info(s"received unhandled message: $message")

  }

  private def updateWorld(results: List[Particle]): Unit = {
    world.updateTime()
    if (worldViewer != null) {
      world.backupPositions(ArrayBuffer(results.toArray: _*))
      world.pushSnapshotToDisplay()

      /* update view */
      worldViewer.updateView()
    } else {
      if (world.getCurrentSteps == this.nSteps) {
        println(s"Done in ${this.chrono.stop().getTime}")
        context.stop(self)
      }

    }


  }
}
