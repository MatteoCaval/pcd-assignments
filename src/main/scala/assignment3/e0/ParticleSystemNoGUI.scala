package assignment3.e0

import akka.actor.ActorSystem
import assignment3.e0.actors.ActorActions.Start
import assignment3.e0.actors.{ActorActions, ControllerActor}


object ParticleSystemNoGUI {

  def main(args: Array[String]): Unit = {
    val nParticles = 1000
    val nSteps: Int = 1000
    val dt = 0.01

    val world = new World(dt)

    val system = ActorSystem("ParticleSystem")
    val controllerActor = system.actorOf(ControllerActor.props(world, nSteps), "ControllerActor")

    controllerActor ! Start(nParticles)
  }

}