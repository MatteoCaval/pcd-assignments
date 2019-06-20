package assignment3.e2.akka

import akka.actor.{ActorSystem, Props}
import assignment3.e2.common.PatchManager
import com.typesafe.config.ConfigFactory

object StartDashboards extends App {

  def startClusterWithDashboard(port: Int) = {
    val system = Utils.getSystemByPort(port)
    system.actorOf(Props[DashboardActor])
  }

  startClusterWithDashboard(2551)

}

object Sensors extends App {

  def startClusterWithSensors(port: Int) = {
    val system = Utils.getSystemByPortWithRole(port, "sensor")
    system.actorOf(SensorActor.props(PatchManager.getRandomPositionInsideMap))
    system.actorOf(SensorActor.props(PatchManager.getRandomPositionInsideMap))
    system.actorOf(SensorActor.props(PatchManager.getRandomPositionInsideMap))
    system.actorOf(SensorActor.props(PatchManager.getRandomPositionInsideMap))
    system.actorOf(SensorActor.props(PatchManager.getRandomPositionInsideMap))
  }

  startClusterWithSensors(2552)


  scala.io.Source.stdin.getLines().foreach { line =>
    val system = Utils.getSystemByPortWithRole(0, "sensor")
    system.actorOf(SensorActor.props(PatchManager.getRandomPositionInsideMap), line)
  }
}

object StartGuardians extends App {


  def startClusterWithGuardians(port: Int) = {
    val system = Utils.getSystemByPortWithRole(port, "guardian")
    system.actorOf(GuardianActor.props(PatchManager.getPatches(0)))
    system.actorOf(GuardianActor.props(PatchManager.getPatches(0)))
    system.actorOf(GuardianActor.props(PatchManager.getPatches(0)))
    system.actorOf(GuardianActor.props(PatchManager.getPatches(1)))
    system.actorOf(GuardianActor.props(PatchManager.getPatches(2)))
    system.actorOf(GuardianActor.props(PatchManager.getPatches(2)))
    system.actorOf(GuardianActor.props(PatchManager.getPatches(3)))
    system.actorOf(GuardianActor.props(PatchManager.getPatches(3)))
  }

  startClusterWithGuardians(0)


}

object StartDashboards2 extends App {

  def startClusterWithDashboard(port: Int) = {
    val system = Utils.getSystemByPort(port)
    system.actorOf(Props[DashboardActor])
  }

  startClusterWithDashboard(0)

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

