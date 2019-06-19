package assignment3.e2.rmi.remoteobjects

import java.io.Serializable
import java.rmi.RemoteException
import java.util.concurrent.ConcurrentHashMap

import assignment3.e2.rmi.mapentry.{DashboardEntry, GuardianEntry, SensorEntry}

@SerialVersionUID(3925347100510516824L)
class MapMonitorImpl(var TAG: String) extends MapMonitor with Serializable {
  private val dashboards: ConcurrentHashMap[String, DashboardEntry] = new ConcurrentHashMap()
  private val guardians: ConcurrentHashMap[String, GuardianEntry] = new ConcurrentHashMap()
  private val sensors: ConcurrentHashMap[String, SensorEntry] = new ConcurrentHashMap()

  @throws[RemoteException]
  override def addDashboard(dashboard: DashboardEntry): Unit = {
    this.dashboards.put(dashboard.getId, dashboard)

    val dashboardObj = dashboard.getRemoteObject
    // Give the dashboard knowledge of sensors
    dashboardObj.setSensors(this.sensors)

    // Give the dashboard knowledge of guardians
    dashboardObj.setGuardians(this.guardians)

    dashboardObj.tell(TAG + "Server connected to dashboard " + dashboard.getId)
  }

  @throws[RemoteException]
  override def addGuardian(guardian: GuardianEntry): Unit = {
    this.guardians.put(guardian.getId, guardian)

    val guardianObj = guardian.getRemoteObject
    // Add the guardian to each dashboard
    dashboards.entrySet.forEach(d => {
      try {
        d.getValue.getRemoteObject.notifyNewGuardian(guardian)
      } catch {
        case e: RemoteException =>
          e.printStackTrace()
      }
    })

    // Add the guardian to each of other of the same patch
    guardians.entrySet.forEach(g => {
      if (!g.getKey.equals(guardian.getId) && g.getValue.getPatchId.equals(guardian.getPatchId)) {
        try {
          g.getValue.getRemoteObject.notifyNewGuardian(guardian)
        } catch {
          case e: RemoteException =>
            e.printStackTrace()
        }
      }
    })

    // Give the guardian knowledge of sensors
    guardianObj.setSensors(this.sensors)

    // Give the guardian knowledge of guardians in same patch
    val samePatchGuardians: ConcurrentHashMap[String, GuardianEntry] = new ConcurrentHashMap()
    guardians.entrySet.forEach(g => {
      if (g.getValue.getPatchId.equals(guardian.getPatchId)) {
        samePatchGuardians.put(g.getKey, g.getValue)
      }
    })
    guardianObj.setPatchGuardians(samePatchGuardians)

    guardianObj.tell(TAG + "Server connected to guardian " + guardian.getId)
  }

  @throws[RemoteException]
  override def addSensor(sensor: SensorEntry): Unit = {
    this.sensors.put(sensor.getId, sensor)

    val sensorObj = sensor.getRemoteObject
    // Add the sensor to each Guardian
    guardians.entrySet().forEach(g => {
      try {
        g.getValue.getRemoteObject.notifyNewSensor(sensor)
      } catch {
        case e: RemoteException =>
          e.printStackTrace()
      }
    })

    // Add the sensor to each Dashboard
    dashboards.entrySet.forEach(d => {
      try {
        d.getValue.getRemoteObject.notifyNewSensor(sensor)
      } catch {
        case e: RemoteException =>
          e.printStackTrace()
      }
    })

    sensorObj.tell(TAG + "Server connected to sensor " + sensor.getId)
  }
}