package assignment3.e2.akka

import java.util.UUID

import akka.actor.{ActorSystem, PoisonPill, Props}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

object Test extends App {

  def startClusterWithSensors(port: Int) = {
    val config = getConfig(port)
    val system = ActorSystem("MapMonitor", config)
    val sensor = system.actorOf(SensorActor.props(P2d(0, 0)), s"sensor")
    val sensor2 = system.actorOf(SensorActor.props(P2d(0, 0)), "sensor2")
    import system.dispatcher
    system.scheduler.schedule(5 seconds, 3 seconds) {
      sensor ! "temperature"
      sensor2 ! "temperature"
    }

  }

  //
  //  def startClusterWithGuardians(port: Int) = {
  //    val config = getConfig(port)
  //    val system = ActorSystem("MapMonitor", config)
  //    val guardian = system.actorOf(GuardianActor.props(Patch(P2d(0, 0), P2d(1, 1))), "guardian")
  //
  //  }
  //

//  for (i <- 1 to 2) {
    startClusterWithSensors(2560 /*+ i*/)
//  }

  //  startClusterWithGuardians(2551)
  //  startClusterWithGuardians(2552)


  def getConfig(port: Int) = ConfigFactory.parseString(
    s"""
       |akka.cluster.roles = ["sensor"]
       |akka.remote.artery.canonical.port = $port
      """.stripMargin)
    .withFallback(ConfigFactory.load("es2.conf"))

}


object Test2 extends App {


  def startClusterWithGuardians(port: Int) = {
    val config = getConfig(port)
    val system = ActorSystem("MapMonitor", config)
    val guardian = system.actorOf(GuardianActor.props(Patch(P2d(0, 0), P2d(1, 1))), "guardian")
    system.actorOf(Props[SensorListenerActor], "sensorListenerActor")

  }

  startClusterWithGuardians(2551)
  //  startClusterWithGuardians(2552)


  def getConfig(port: Int) = ConfigFactory.parseString(
    s"""
       |akka.remote.artery.canonical.port = $port
      """.stripMargin)
    .withFallback(ConfigFactory.load("es2.conf"))

}

