package assignment3.e1

import akka.actor.ActorSystem
import assignment3.e1.actors.ActorMessages.Start
import assignment3.e1.actors.{ActorMessages, ControllerActor}


object ParticleSystemNoGUI extends App {
  val nParticles = 1000
  val nSteps: Int = 1000
  val dt = 0.01

  val world = new World(dt)

  val system = ActorSystem("ParticleSystem")
  val controllerActor = system.actorOf(ControllerActor.props(world, nSteps), "ControllerActor")

  controllerActor ! Start(nParticles)

}
