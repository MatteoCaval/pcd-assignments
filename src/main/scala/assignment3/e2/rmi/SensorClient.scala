package assignment3.e2.rmi

import java.rmi.registry.LocateRegistry
import java.rmi.server.UnicastRemoteObject
import java.util.UUID

import assignment3.e2.rmi.mapentry.SensorEntry
import assignment3.e2.rmi.remoteobjects.{MapMonitor, Sensor, SensorImpl}

import scala.util.Random

object SensorClient extends App {
  val TAG = "SensorClient - "

  //if (args.length < 1) null else args(0)
  val id = if (args.length < 2) UUID.randomUUID().toString else args(0)
  val port: Int = if (args.length < 2) Random.nextInt(255) else args(1).toInt

  var sensor: Sensor = _

  try {
    println(TAG + "Started")

    val registry = LocateRegistry.getRegistry(Config.RMI_DEFAULT_HOST, Config.RMI_DEFAULT_PORT)
    val server = registry.lookup("monitorObj").asInstanceOf[MapMonitor]

    sensor = new SensorImpl(id)
    UnicastRemoteObject.exportObject(sensor, port)

    server.addSensor(SensorEntry(id, sensor))
  } catch {
    case e: Exception =>
      println(TAG + "Exception: " + e)
      e.printStackTrace()
  }
}