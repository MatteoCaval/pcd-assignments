package assignment3.e0.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Stash}
import assignment3.e0.Particle
import assignment3.e0.actors.ActorActions._

import scala.collection.mutable

object ParticleMasterActor {
  def props(controllerRef: ActorRef) = Props(new ParticleMasterActor(controllerRef))
}

class ParticleMasterActor(private val controllerActor: ActorRef) extends Actor with ActorLogging with Stash {

  var resultsNumber = 0
  var particleWorkers: Seq[ActorRef] = Seq()
  val results: mutable.MutableList[Particle] = mutable.MutableList()

  override def receive: Receive = handleParticle

  def handleParticle: Receive = {
    case AddParticle(particle) =>
      log.info("adding new particle")
      val newParticle = context.actorOf(ParticleActor.props(particle))
      this.particleWorkers = particleWorkers :+ newParticle

    case Compute(particles) =>
      log.info(s"compute command received, number of slave actors: ${particleWorkers.length}")

      this.sendComputationToParticles(particles)
      context.become(receiveResults)

    case ComputeNext =>
      this.sendComputationToParticles(results)
      context.become(receiveResults)

    case RemoveParticle =>
      particleWorkers = particleWorkers.tail

    case message => log.info(s"received $message")

  }

  def receiveResults: Receive = {
    case ParticleResult(result) =>
      resultsNumber += 1
      results += result
      //        log.info(s"received result $result, total: $resultsNumber")
      if (resultsNumber == particleWorkers.length) {
        //        log.info(s"all ${particleWorkers.length} results received, final result: ${results.map(p => p.getPos.toString)}")
        controllerActor ! ComputationDone(results.toList)
        unstashAll()
        context.become(handleParticle)
      }

    case message =>
      log.info(s"received $message i cannot handle")
      stash()


  }

  private def reset = {
    resultsNumber = 0
    results.clear()
  }

  private def sendComputationToParticles(particles: Seq[Particle]): Unit = {
    reset
    particleWorkers.foreach(p => p ! ComputeParticle(particles))
  }

}
