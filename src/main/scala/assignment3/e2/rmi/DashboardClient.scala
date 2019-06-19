package assignment3.e2.rmi

import java.rmi.registry.LocateRegistry
import java.rmi.server.UnicastRemoteObject
import java.util.UUID

import assignment3.e2.common.{MapMonitorViewImpl, PatchManager, ViewListener}
import assignment3.e2.rmi.mapentry.DashboardEntry
import assignment3.e2.rmi.remoteobjects.{Dashboard, DashboardImpl, MapMonitor}

import scala.util.Random

object DashboardClient extends App {
  val TAG = "DashboardClient - "

  //if (args.length < 1) null else args(0)
  val id = if (args.length < 2) UUID.randomUUID().toString else args(0)
  val port: Int = if (args.length < 2) Random.nextInt(255) else args(1).toInt
  var dashboard: Dashboard = _

  try {
    println(TAG + "Started")

    val registry = LocateRegistry.getRegistry(Config.RMI_DEFAULT_HOST, Config.RMI_DEFAULT_PORT)
    val server = registry.lookup("monitorObj").asInstanceOf[MapMonitor]

    val view = new MapMonitorViewImpl(new ViewListener {
      override def resetAlarmPressed(patchId: Int): Unit = {
        println(s"Alarm switched off for: $patchId")
        dashboard.notifyAlarmOff(patchId)
      }
    }, PatchManager.getPatches.size)

    view.show()

    dashboard = new DashboardImpl(id, view)
    UnicastRemoteObject.exportObject(dashboard, port)

    server.addDashboard(DashboardEntry(id, dashboard))
  } catch {
    case e: Exception =>
      println(TAG + "Exception: " + e)
      e.printStackTrace()
  }

  while (true) {
    Thread.sleep(1000)
    dashboard.update()
  }
}