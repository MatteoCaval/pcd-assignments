package assignment3.e0.actors

import akka.actor.{ActorSystem, Props}
import assignment3.e0.actors.ActorActions.Start


object TestWithParticles extends App {

  import assignment3.e0._

  //  case class AddParticle(particle: Particle)
  //
  //  case class AddParticles(numberOfParticle: Int)
  //
  //  case class Compute(particles: Seq[Particle])
  //
  //  case object ComputeNext
  //
  //  case object ComputationDone
  //
  //  case class ComputeParticle(particles: Seq[Particle])
  //
  //  case class ParticleResult(particle: Particle)
  //
  //  case object Start


  //  class Controller extends Actor with ActorLogging {
  //
  //    var particleMaster: ActorRef = _
  //
  //    override def receive: Receive = {
  //      case Start =>
  //        log.info("simulation started")
  //        particleMaster = context.actorOf(Props[ParticleMaster])
  //
  //        //particles created and added to master
  //        val particles = Utils.createNParticles(5000)
  //        log.info(s"particles created: ${particles.map(p => p.getPos.toString)}")
  //        particles.foreach(p => particleMaster ! AddParticle(p))
  //
  //        Thread.sleep(4000)
  //
  //        particleMaster ! Compute(particles)
  //        context.become(computation)
  //    }
  //
  //    def computation: Receive = {
  //      case ComputationDone =>
  //        log.info("computation done by master")
  //              particleMaster ! ComputeNext
  //    }
  //
  //  }


  //  class ParticleMaster extends Actor with ActorLogging {
  //
  //    var resultsNumber = 0
  //    var particleWorkers: Seq[ActorRef] = Seq()
  //    val results: mutable.MutableList[Particle] = mutable.MutableList()
  //    var controller: ActorRef = _
  //
  //    override def receive: Receive = handleParticle(Seq())
  //
  //    def handleParticle(particleWorkers: Seq[ActorRef]): Receive = {
  //      case AddParticle(particle) =>
  //        log.info("adding new particle")
  //        val newParticle = context.actorOf(ParticleActor.props(particle))
  //        context.become(handleParticle(particleWorkers :+ newParticle))
  //
  //      case Compute(particles) =>
  //        log.info(s"compute command received, number of slave actors: ${particleWorkers.length}")
  //
  //        controller = sender
  //        this.particleWorkers = particleWorkers
  //
  //        this.sendComputationToParticles(particles)
  //        context.become(receiveResults)
  //
  //      case ComputeNext =>
  //        this.sendComputationToParticles(results)
  //        context.become(receiveResults)
  //
  //    }
  //
  //    def receiveResults: Receive = {
  //      case ParticleResult(result) =>
  //        resultsNumber += 1
  //        results += result
  //        //        log.info(s"received result $result, total: $resultsNumber")
  //        if (resultsNumber == particleWorkers.length) {
  //          log.info(s"all ${particleWorkers.length} results received, final result: ${results.map(p => p.getPos.toString)}")
  //          controller ! ComputationDone
  //          context.become(handleParticle(particleWorkers))
  //        }
  //    }
  //
  //    private def reset = {
  //      resultsNumber = 0
  //      results.clear()
  //    }
  //
  //    private def sendComputationToParticles(particles: Seq[Particle]): Unit = {
  //      reset
  //      particleWorkers.foreach(p => p ! ComputeParticle(particles))
  //    }
  //
  //  }

  //  object ParticleActor {
  //    def props(p: Particle) = Props(new ParticleActor(new Particle(p.getPos, p.getVel, 1, 1, 1)))
  //  }

  //  class ParticleActor(private var particle: Particle) extends Actor with ActorLogging {
  //
  //    override def receive: Receive = {
  //      case ComputeParticle(particles) =>
  ////        log.info(s"${self.path} received command")
  //        val force = Utils.computeForce(particle, particles)
  //        particle.update(force, Utils.dt)
  //        sender ! ParticleResult(particle)
  //      case message =>
  //        log.info(message.toString)
  //    }
  //
  //  }


  val system = ActorSystem("TestAss")
  val controller = system.actorOf(Props[ControllerActor])

  controller ! Start


}

//object Utils {
//
//  import assignment3.e0._
//
//  val dt = 0.01
//  private val kParam = 1
//
//  def createNParticles(nParticles: Int): Seq[Particle] = {
//    val random = new Random(System.currentTimeMillis())
//    for (_ <- 1 to nParticles) yield {
//      val x = random.nextDouble() * 2 - 1
//      val y = random.nextDouble() * 2 - 1
//      new Particle(new P2d(x, y), new V2d(0, 0), 1, 1, 1)
//    }
//  }
//
//  def computeForce(p: Particle, particles: Seq[Particle]): V2d = {
//    var force = new V2d(0, 0)
//    val alpha: Double = p.getAlpha
//
////    println(s"calculating force of particle with pos ${p.getPos}, total particles: ${particles.length}")
//
//    for (i <- particles.indices) {
//      val p2 = particles(i)
//      if (p.getPos.x != p2.getPos.x || p.getPos.y != p2.getPos.y )  {
//        val pos2: P2d = p2.getPos
//        val ds: V2d = p.getPos sub pos2
//        val distance: Double = ds.abs
//        val invd: Double = 1.0 / (distance * distance * distance)
//        val f: V2d = ds.mul(kParam).mul(alpha).mul(p2.getAlpha).mul(invd)
//        force = force.sum(f)
//      }
//    }
//
//    force = force.sum(p.getVel.mul(-p.getAttrCoeff))
////    println(s"Force: $force")
//    force
//
//  }
//
//
//}
