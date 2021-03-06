package assignment3.e1.actors

import assignment3.e1.{P2d, Particle}

object ActorMessages {

  case class AddParticle(particle: Particle)

  case class AddParticles(particles: Seq[Particle]) 

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

  // region App modes

  sealed class Mode

  case object StepByStepMode extends Mode

  case object ContinuousMode extends Mode


  // endregion
}
