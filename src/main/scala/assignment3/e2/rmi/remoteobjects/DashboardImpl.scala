package assignment3.e2.rmi.remoteobjects

import java.io.Serializable
import java.rmi.RemoteException
import java.util.concurrent.ConcurrentHashMap

import assignment3.e2.common.{Config, GuardianStateEnum, MapMonitorViewImpl}
import assignment3.e2.rmi.mapentry.{GuardianEntry, SensorEntry}
import javax.swing.SwingUtilities

@SerialVersionUID(5377073057466013968L)
class DashboardImpl(var name: String, var view:MapMonitorViewImpl) extends Dashboard with Serializable {
  private val guardians: ConcurrentHashMap[String, GuardianEntry] = new ConcurrentHashMap()
  private val sensors: ConcurrentHashMap[String, SensorEntry] = new ConcurrentHashMap()

  private val brokenSensors: ConcurrentHashMap[String, Long] = new ConcurrentHashMap()
  private val brokenGuardians: ConcurrentHashMap[String, Long] = new ConcurrentHashMap()

  @throws[RemoteException]
  override def tell(text: String): Unit = {
    println(text)
  }

  @throws[RemoteException]
  override def notifyNewGuardian(guardian: GuardianEntry): Unit = {
    guardians.put(guardian.getId, guardian)
  }

  @throws[RemoteException]
  override def notifyNewSensor(sensor: SensorEntry): Unit = {
    sensors.put(sensor.getId, sensor)
  }

  @throws[RemoteException]
  override def setSensors(systemSensors: ConcurrentHashMap[String, SensorEntry]): Unit = {
    sensors.putAll(systemSensors)
  }

  @throws[RemoteException]
  override def setGuardians(systemGuardians: ConcurrentHashMap[String, GuardianEntry]): Unit = {
    guardians.putAll(systemGuardians)
  }

  @throws[RemoteException]
  override def update(): Unit = {
    guardians.entrySet().forEach(g => {
      val guardianObj = g.getValue.getRemoteObject
      try {
        val guardianState = guardianObj.getGuardiansStatus
        view.notifyGuardian(guardianState)
        eventuallyRemoveFromBrokenGuardians(g.getKey)
        if (guardianState.state == GuardianStateEnum.ALARM){
          view.notifyAlarmStateEnabled(guardianState.patch.id, enabled = true)
        }
        //println(state.averageTemp)
      } catch {
        case _: Exception =>
          checkForBrokenGuardian(g.getKey, g.getValue.getPatchId)
      }
    })

    sensors.entrySet().forEach(s => {
      val sensorObj = s.getValue.getRemoteObject

      SwingUtilities.invokeLater(() => {
        try {
          val pos = sensorObj.getDashboardPosition
          view.notifySensor(pos)
          eventuallyRemoveFromBrokenSensors(s.getKey)
        } catch {
          case _: Exception => checkForBrokenSensor(s.getKey)
        }
      })
    })
  }

  @throws[RemoteException]
  override def notifyAlarmOff(patchId: Int): Unit = {
    guardians.entrySet().forEach(g => {
      val guardian = g.getValue
      if (guardian.getPatchId == patchId){
        val guardianObj = guardian.getRemoteObject
        try {
          guardianObj.setState(GuardianStateEnum.IDLE)
          eventuallyRemoveFromBrokenGuardians(g.getKey)

        } catch {
          case e: Exception =>
            println(e.printStackTrace())
            checkForBrokenGuardian(guardian.getId, guardian.getPatchId)
        }
      }
    })
  }

  private def eventuallyRemoveFromBrokenGuardians(guardianId: String) = {
    if (brokenGuardians.contains(guardianId)) brokenGuardians.remove(guardianId)
  }

  private def eventuallyRemoveFromBrokenSensors(sensorId: String) = {
    if (brokenSensors.contains(sensorId)) brokenGuardians.remove(sensorId)
  }

  private def checkForBrokenGuardian(guardianId: String, patchId: Int): Unit = {
    val currentTime = System.currentTimeMillis()
    if (brokenGuardians.containsKey(guardianId)) {
      if (currentTime - brokenGuardians.get(guardianId) > Config.MAX_RETRY_TIME) {
        guardians.remove(guardianId)
        view.notifyGuardianRemoved(guardianId, patchId)
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
        view.notifySensorRemoved(sensorId)
      }
    } else {
      brokenSensors.put(sensorId, currentTime)
    }
  }

}