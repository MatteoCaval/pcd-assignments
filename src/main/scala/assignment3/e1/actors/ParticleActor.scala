package assignment3.e1.actors

import akka.actor.{Actor, ActorLogging, Props}
import assignment3.e1.Particle
import assignment3.e1.actors.ActorActions.{ComputeParticle, ParticleResult}

object ParticleActor {
  def props(p: Particle) = Props(new ParticleActor(new Particle(p.getPos, p.getVel, 1, 1, 1)))
}

class ParticleActor(private var particle: Particle) extends Actor with ActorLogging {

  override def receive: Receive = {
    case ComputeParticle(particles) =>
      //        log.info(s"${self.path} received command")
      val force = ParticleComputationUtils.computeForce(particle, particles)
      particle.update(force, ParticleComputationUtils.dt)
      sender ! ParticleResult(particle)
    case message =>
      log.info(message.toString)
  }
}