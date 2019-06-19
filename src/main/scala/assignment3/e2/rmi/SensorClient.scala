package assignment3.e2.rmi

import java.rmi.registry.LocateRegistry
import java.rmi.server.UnicastRemoteObject
import java.util.UUID

import assignment3.e2.rmi.stub_scheleton.{MapMonitor, Sensor, SensorImpl, SensorStr}

import scala.util.Random

object SensorClient extends App {
  val TAG = "SensorClient - "

  val host = null
  //if (args.length < 1) null else args(0)
  val id = if (args.length < 2) UUID.randomUUID().toString else args(0)
  val port: Int = if (args.length < 2) Random.nextInt(255) else args(1).toInt

  var sensor: Sensor = _

  try {
    println(TAG + "Started")

    val registry = LocateRegistry.getRegistry("169.254.38.10", 1500)
    val server = registry.lookup("monitorObj").asInstanceOf[MapMonitor]

    sensor = new SensorImpl(id)
    UnicastRemoteObject.exportObject(sensor, port)

    server.addSensor(SensorStr(id, sensor))
  } catch {
    case e: Exception =>
      println(TAG + "Exception: " + e)
      e.printStackTrace()
  }
}