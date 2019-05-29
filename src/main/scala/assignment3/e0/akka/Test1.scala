package assignment3.e0.akka

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

import scala.collection.mutable


object Test1 extends App{


//  case object AddParticle
//
//  case class AddParticles(numberOfParticle: Int)
//
//  case object Compute
//
//  case object ComputationDone
//
//  case class ParticleResult(result: Int)
//
//  case object Start
//
//
//  class Master extends Actor with ActorLogging {
//
//    override def receive: Receive = handleParticle(Seq())
//
//    def handleParticle(particleWorkers: Seq[ActorRef]): Receive = {
//      case AddParticle =>
//        log.info("adding new particle")
//        val newParticle = context.actorOf(Props[ParticleActor])
//        context.become(handleParticle(particleWorkers :+ newParticle))
//      case Compute =>
//        log.info("compute command received")
//        particleWorkers.foreach(p => p ! Compute)
//        context.become(receiveResults(0, particleWorkers, List()))
//    }
//
//    def receiveResults(resultsNumber: Int, particleWorkers: Seq[ActorRef], results: List[Int]): Receive = {
//      case ParticleResult(result) =>
//        log.info(s"received result $result")
//        val newTotalResulsNumber = resultsNumber + 1
//        val newResults = results :+ result
//        if (newTotalResulsNumber == particleWorkers.length) {
//          log.info(s"all ${particleWorkers.length} results received, final result: ${newResults.sum}")
//          context.become(handleParticle(particleWorkers))
//          //          self ! Compute
//        } else {
//          context.become(receiveResults(newTotalResulsNumber, particleWorkers, newResults))
//        }
//    }
//
//  }
//
//  class Controller extends Actor with ActorLogging {
//
//    var particleMaster: ActorRef = _
//
//    override def receive: Receive = {
//      case Start =>
//        log.info("simulation started")
//        particleMaster = context.actorOf(Props[ParticleMaster])
//        for (_ <- 1 to 10000) {
//          particleMaster ! AddParticle
//        }
//        Thread.sleep(1000)
//        particleMaster ! Compute
//        context.become(computation)
//    }
//
//    def computation: Receive = {
//      case ComputationDone =>
//        log.info("computation done by master")
//        particleMaster ! Compute
//    }
//
//  }
//
//
//  class ParticleMaster extends Actor with ActorLogging {
//
//    var resultsNumber = 0
//    var particleWorkers: Seq[ActorRef] = Seq()
//    val results: mutable.MutableList[Int] = mutable.MutableList()
//    var controller: ActorRef = _
//
//    override def receive: Receive = handleParticle(Seq())
//
//    def handleParticle(particleWorkers: Seq[ActorRef]): Receive = {
//      case AddParticle =>
//        log.info("adding new particle")
//        val newParticle = context.actorOf(Props[ParticleActor])
//        context.become(handleParticle(particleWorkers :+ newParticle))
//      case Compute =>
//        reset
//        log.info("compute command received")
//        controller = sender
//        particleWorkers.foreach(p => p ! Compute)
//        this.particleWorkers = particleWorkers
//        context.become(receiveResults)
//    }
//
//    def receiveResults: Receive = {
//      case ParticleResult(result) =>
//        resultsNumber += 1
//        results += result
//        //        log.info(s"received result $result, total: $resultsNumber")
//        if (resultsNumber == particleWorkers.length) {
//          log.info(s"all ${particleWorkers.length} results received, final result: ${results.sum}")
//          controller ! ComputationDone
//          context.become(handleParticle(particleWorkers))
//        }
//    }
//
//    def reset = {
//      resultsNumber = 0
//      results.clear()
//    }
//
//  }
//
//  class ParticleActor extends Actor with ActorLogging {
//
//    override def receive: Receive = {
//      case Compute =>
//        //        log.info(s"sending result from ${self.path}")
//        sender ! ParticleResult(5)
//    }
//
//  }
//
//
//  val system = ActorSystem("TestAss")
//  val controller = system.actorOf(Props[Controller])
//
//  controller ! Start


}
