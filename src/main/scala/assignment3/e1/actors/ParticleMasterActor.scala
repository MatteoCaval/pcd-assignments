package assignment3.e1.actors

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props, Stash}
import assignment3.e1.Particle
import assignment3.e1.actors.ActorActions._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object ParticleMasterActor {
  def props(controllerRef: ActorRef) = Props(new ParticleMasterActor(controllerRef))
}

class ParticleMasterActor(private val controllerActor: ActorRef) extends Actor with ActorLogging with Stash {

  var resultsNumber = 0
  var particleWorkers: Seq[ActorRef] = Seq()
  val particleResults: ArrayBuffer[Particle] = ArrayBuffer()

  override def receive: Receive = handleParticle

  def handleParticle: Receive = {

    case AddParticles(particles) =>
      log.info(s"adding ${particles.length} particles")
      val particlesRef = particles.map(p =>
        context.actorOf(ParticleActor.props(p))
      )
      this.particleWorkers = particleWorkers ++ particlesRef

    case AddParticle(particle) =>
      log.info("adding new particle")
      val newParticle = context.actorOf(ParticleActor.props(particle))
      this.particleWorkers = particleWorkers :+ newParticle
      log.info(s"before ${particleResults.length}")
      particleResults += particle
      log.info(s"after ${particleResults.length}")

    case Compute(particles) => // FIXME
      log.info(s"compute command received, number of slave actors: ${particleWorkers.length}")

      if (particles.nonEmpty) {
        this.sendComputationToParticles(particles)
        context.become(receiveResults)
      }


    case ComputeNext => //FIXME
      if (particleWorkers.nonEmpty) {
        this.sendComputationToParticles(particleResults)
        context.become(receiveResults)
      } else {
        notifyComputationCompleted
      }

    case RemoveParticle =>
      log.info("received remove message")
      if (particleWorkers.length == 1) {
        particleWorkers = Seq()
        reset
        notifyComputationCompleted

      } else {
//        context.stop(particleWorkers.head)
        particleWorkers.head ! PoisonPill // da testare
        particleWorkers = particleWorkers.tail
      }

    case message => log.info(s"received $message")

  }

  def receiveResults: Receive = {
    case ParticleResult(result) =>
      resultsNumber += 1
      particleResults += result
      //        log.info(s"received result $result, total: $resultsNumber")
      if (resultsNumber == particleWorkers.length) {
        //        log.info(s"all ${particleWorkers.length} results received, final result: ${results.map(p => p.getPos.toString)}")
        notifyComputationCompleted
      }

    case message =>
      log.info(s"received $message i cannot handle and stashing")
      stash()
  }

  private def reset = {
    resultsNumber = 0
    particleResults.clear()
  }

  private def sendComputationToParticles(particles: Seq[Particle]): Unit = { // FIXME
    val list: ArrayBuffer[Particle] = ArrayBuffer()
    particles.foreach(p => list += p)
    reset
    particleWorkers.foreach(p => p ! ComputeParticle(list))

  }

  private def notifyComputationCompleted = {
    controllerActor ! ComputationDone(particleResults.toList)
    unstashAll()
    context.become(handleParticle)
  }

}
