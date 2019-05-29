package assignment3.e0.actors

import assignment3.e0.{P2d, Particle}

object ActorActions {

  case class AddParticle(particle: Particle)

  case class AddParticles(numberOfParticle: Int) // not used yet

  case class ComputationDone(results: List[Particle])

  case class ComputeParticle(particles: Seq[Particle])

  case class ParticleResult(particle: Particle)


  // region messages from the ControllerActor to the ParticleActor

  case class Compute(particles: Seq[Particle])

  case object ComputeNext

  // endregion


  // region messages to the ControllerActor

  case class Start(nParticles: Int)

  case object Stop

  case class AddParticleByPosition(position: P2d)

  case object RemoveParticle

  // endregion

}
