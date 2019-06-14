package assignment3.e2.akka

import java.util.UUID

import akka.actor.{ActorSystem, PoisonPill, Props}
import assignment3.e2.common.{P2d, Patch}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

object Sensors extends App {

  def startClusterWithSensors(port: Int) = {
    val system = Utils.getSystemByPortWithRole(port, "sensor")
    system.actorOf(SensorActor.props(P2d(0, 0)))
    system.actorOf(SensorActor.props(P2d(0, 0)))
    system.actorOf(SensorActor.props(P2d(0, 0)))
    import system.dispatcher
    //    system.scheduler.schedule(5 seconds, 10 seconds) {
    //      sensor ! "temperature"
    //    }
  }

  startClusterWithSensors(2551 /*+ i*/)
  startClusterWithSensors(2552)
  startClusterWithSensors(2560 /*+ i*/)
  //  startClusterWithSensors(2561 /*+ i*/)



  scala.io.Source.stdin.getLines().foreach { line =>
    val system = Utils.getSystemByPortWithRole(0, "sensor")
    system.actorOf(SensorActor.props(P2d(1, 1)), line)
  }
}

object Ehi extends App {
  Utils.getSystemByPort(0).actorOf(Props[TestActor], "sensorRegistrationTest")
}


object StartGuardians extends App {


  def startClusterWithGuardians(port: Int) = {
    val system = Utils.getSystemByPortWithRole(port, "guardian")
    val guardian = system.actorOf(GuardianActor.props(Patch(P2d(0, 0), P2d(1, 1))), "guardian")
  }

  startClusterWithGuardians(2580)
  //  startClusterWithGuardians(2552)

}


object StartDashboards extends App {

  def startClusterWithDashboard(port: Int) = {
    val system = Utils.getSystemByPort(port)
    system.actorOf(Props[DashboardActor])
  }

  startClusterWithDashboard(0)

}


object StartGuardianListener extends App {
  val system = Utils.getSystemByPort(2551)
  system.actorOf(Props[GuardianListenerActor], "guardianListenerActor")
}

object Utils {

  def getSystemByPort(port: Int): ActorSystem = {
    val config = Utils.getConfig(port)
    ActorSystem("MapMonitor", config)
  }

  def getSystemByPortWithRole(port: Int, role: String): ActorSystem = {
    val config = Utils.getConfigWithRole(port, role)
    ActorSystem("MapMonitor", config)
  }


  def getConfig(port: Int) = ConfigFactory.parseString(
    s"""
       |akka.remote.netty.tcp.port = $port
      """.stripMargin)
    .withFallback(ConfigFactory.load("es2.conf"))


  def getConfigWithRole(port: Int, role: String) = ConfigFactory.parseString(
    s"""
       |akka.cluster.roles = ["$role"]
       |akka.remote.netty.tcp.port = $port
      """.stripMargin)
    .withFallback(ConfigFactory.load("es2.conf"))

}

