package assignment3.e2.rmi.remoteobjects

import java.io.Serializable
import java.rmi.RemoteException
import java.util.concurrent.ConcurrentHashMap

import assignment3.e2.rmi.Config
import assignment3.e2.rmi.mapentry.{DashboardEntry, GuardianEntry, SensorEntry}

@SerialVersionUID(3925347100510516824L)
class MapMonitorImpl(var TAG: String) extends MapMonitor with Serializable {
  private val dashboards: ConcurrentHashMap[String, DashboardEntry] = new ConcurrentHashMap()
  private val guardians: ConcurrentHashMap[String, GuardianEntry] = new ConcurrentHashMap()
  private val sensors: ConcurrentHashMap[String, SensorEntry] = new ConcurrentHashMap()

  private val brokenSensors: ConcurrentHashMap[String, Long] = new ConcurrentHashMap()
  private val brokenGuardians: ConcurrentHashMap[String, Long] = new ConcurrentHashMap()
  private val brokenDashboards: ConcurrentHashMap[String, Long] = new ConcurrentHashMap()



  @throws[RemoteException]
  override def addDashboard(dashboard: DashboardEntry): Unit = {
    this.dashboards.put(dashboard.getId, dashboard)

    val dashboardObj = dashboard.getRemoteObject
    try {
      // Give the dashboard knowledge of sensors
      dashboardObj.setSensors(this.sensors)

      // Give the dashboard knowledge of guardians
      dashboardObj.setGuardians(this.guardians)

      dashboardObj.tell(TAG + "Server connected to dashboard " + dashboard.getId)
      if (brokenDashboards.contains(dashboard.getId)) brokenDashboards.remove(dashboard.getId)
    } catch {
      case _: Exception => checkForBrokenDashboard(dashboard.getId)
    }
  }

  @throws[RemoteException]
  override def addGuardian(guardian: GuardianEntry): Unit = {
    this.guardians.put(guardian.getId, guardian)

    val guardianObj = guardian.getRemoteObject
    // Add the guardian to each dashboard
    dashboards.entrySet.forEach(d => {
      try {
        d.getValue.getRemoteObject.notifyNewGuardian(guardian)
        if (brokenDashboards.contains(d.getKey)) brokenDashboards.remove(d.getKey)
      } catch {
        case _: Exception => checkForBrokenDashboard(d.getKey)
      }
    })

    // Add the guardian to each of other of the same patch
    guardians.entrySet.forEach(g => {
      if (!g.getKey.equals(guardian.getId) && g.getValue.getPatchId.equals(guardian.getPatchId)) {
        try {
          g.getValue.getRemoteObject.notifyNewGuardian(guardian)
          if (brokenGuardians.contains(g.getKey)) brokenGuardians.remove(g.getKey)
        } catch {
          case _: Exception => checkForBrokenGuardian(g.getKey)
        }
      }
    })

    // Give the guardian knowledge of guardians in same patch
    val samePatchGuardians: ConcurrentHashMap[String, GuardianEntry] = new ConcurrentHashMap()
    guardians.entrySet.forEach(g => {
      if (g.getValue.getPatchId.equals(guardian.getPatchId)) {
        samePatchGuardians.put(g.getKey, g.getValue)
      }
    })

    try {
      // Give the guardian knowledge of sensors
      guardianObj.setSensors(this.sensors)

      guardianObj.setPatchGuardians(samePatchGuardians)

      guardianObj.tell(TAG + "Server connected to guardian " + guardian.getId)
      if (brokenGuardians.contains(guardian.getId)) brokenGuardians.remove(guardian.getId)
    } catch {
      case _: Exception => checkForBrokenGuardian(guardian.getId)
    }
  }

  @throws[RemoteException]
  override def addSensor(sensor: SensorEntry): Unit = {
    this.sensors.put(sensor.getId, sensor)

    val sensorObj = sensor.getRemoteObject
    // Add the sensor to each Guardian
    guardians.entrySet().forEach(g => {
      try {
        g.getValue.getRemoteObject.notifyNewSensor(sensor)
        if (brokenGuardians.contains(g.getKey)) brokenGuardians.remove(g.getKey)
      } catch {
        case _: Exception => checkForBrokenGuardian(g.getKey)
      }
    })

    // Add the sensor to each Dashboard
    dashboards.entrySet.forEach(d => {
      try {
        d.getValue.getRemoteObject.notifyNewSensor(sensor)
        if (brokenDashboards.contains(d.getKey)) brokenDashboards.remove(d.getKey)
      } catch {
        case _: Exception => checkForBrokenDashboard(d.getKey)
      }
    })

    try {
      sensorObj.tell(TAG + "Server connected to sensor " + sensor.getId)
      if (brokenSensors.contains(sensor.getId)) brokenSensors.remove(sensor.getId)
    } catch {
      case _: Exception => checkForBrokenSensor(sensor.getId)
    }
  }

  private def checkForBrokenGuardian(guardianId: String): Unit = {
    val currentTime = System.currentTimeMillis()
    if (brokenGuardians.containsKey(guardianId)) {
      if (currentTime - brokenGuardians.get(guardianId) > Config.MAX_RETRY_TIME) {
        guardians.remove(guardianId)
      }
    } else {
      brokenGuardians.put(guardianId, currentTime)
    }
  }

  private def checkForBrokenSensor(sensorId: String): Unit = {
    val currentTime = System.currentTimeMillis()
    if (brokenSensors.containsKey(sensorId)) {
      if (currentTime - brokenSensors.get(sensorId) > Config.MAX_RETRY_TIME) {
        sensors.remove(sensorId)
      }
    } else {
      brokenSensors.put(sensorId, currentTime)
    }
  }

  private def checkForBrokenDashboard(dashboardId: String): Unit = {
    val currentTime = System.currentTimeMillis()
    if (brokenDashboards.containsKey(dashboardId)) {
      if (currentTime - brokenDashboards.get(dashboardId) > Config.MAX_RETRY_TIME) {
        sensors.remove(dashboardId)
      }
    } else {
      brokenDashboards.put(dashboardId, currentTime)
    }
  }
}