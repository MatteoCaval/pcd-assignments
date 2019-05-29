package assignment3.e0.akka

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import assignment3.e0.akka.ActorActions._

class ControllerActor extends Actor with ActorLogging {

  var particleMaster: ActorRef = _

  override def receive: Receive = {
    case Start =>
      log.info("simulation started")
      particleMaster = context.actorOf(Props[ParticleMasterActor])

      //particles created and added to master
      val particles = ParticleComputationUtils.createNParticles(5000)
      log.info(s"particles created: ${particles.map(p => p.getPos.toString)}")
      particles.foreach(p => particleMaster ! AddParticle(p))

      Thread.sleep(4000)

      particleMaster ! Compute(particles)
      context.become(computation)
  }

  def computation: Receive = {
    case ComputationDone =>
      log.info("computation done by master")
      particleMaster ! ComputeNext
  }

}
