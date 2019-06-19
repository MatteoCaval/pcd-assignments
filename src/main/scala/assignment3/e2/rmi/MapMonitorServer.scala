package assignment3.e2.rmi

import java.rmi.registry.LocateRegistry
import java.rmi.server.UnicastRemoteObject

import assignment3.e2.rmi.remoteobjects.{MapMonitor, MapMonitorImpl}

object MapMonitorServer extends App {
  private val TAG = "MapMonitorServer - "

  try {
    val monitorObj = new MapMonitorImpl(TAG)
    val monitorObjStub = UnicastRemoteObject.exportObject(monitorObj, 1).asInstanceOf[MapMonitor]
    System.setProperty("java.rmi.server.hostname", Config.RMI_DEFAULT_HOST)

    val registry = LocateRegistry.createRegistry(Config.RMI_DEFAULT_PORT)
    registry.rebind("monitorObj", monitorObjStub)

    System.out.println(TAG + "Started")
  } catch {
    case e: Exception =>
      println(TAG + "Failed: " + e)
  }

}