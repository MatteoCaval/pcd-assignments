package assignment3.e0.actors

import assignment3.e0.Particle

object ActorActions {

  case class AddParticle(particle: Particle)

  case class AddParticles(numberOfParticle: Int)

  case class Compute(particles: Seq[Particle])

  case object ComputeNext

  case class ComputationDone(results: List[Particle])

  case class ComputeParticle(particles: Seq[Particle])

  case class ParticleResult(particle: Particle)

  case class Start(nParticles: Int)

}
