package assignment3.e0

import akka.actor.{ActorSystem, Props}
import assignment3.e0.actors.ActorActions.{AddParticleByPosition, RemoveParticle, Start, Stop}
import assignment3.e0.actors.{ActorActions, ControllerActor}


class Controller(var world: World, var viewer: WorldViewer) {
  private val system = ActorSystem("ParticleSystem")
  private val controllerActor = system.actorOf(ControllerActor.props(world, viewer, ActorActions.ContinuousMode))

  import ActorActions._

  def notifyStarted(nParticles: Int): Unit = {

    controllerActor ! Start(nParticles)

  }

  def notifyStopped = controllerActor ! Stop

  def notifyNewParticle(pos: P2d): Unit =
  /* adding a particle with 100 times the mass and 10 times the charge */
  //    newParticles.notifyNewParticle(new Particle(pos, new V2d(0, 10), 1000, 10, 1))
    controllerActor ! AddParticleByPosition(pos)


  def notifyParticleRemoved = controllerActor ! RemoveParticle

  def notifyContinuousMode = controllerActor ! ContinuousMode

  def notifyStepByStepMode = controllerActor ! StepByStepMode

  def notifyNextStep = controllerActor ! ComputeNext

}