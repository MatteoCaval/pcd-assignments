package assignment3.e2.rmi.remoteobjects

import java.io.Serializable
import java.rmi.RemoteException
import java.util.concurrent.ConcurrentHashMap
import java.util.{Timer, TimerTask}

import assignment3.e2.common.GuardianStateEnum.GuardianStateEnum
import assignment3.e2.common._
import assignment3.e2.rmi.{Config, SensorDetection, StateMessage}
import assignment3.e2.rmi.mapentry.{GuardianEntry, SensorEntry}

@SerialVersionUID(5377073057466013968L)
class GuardianImpl(private var id: String, private var patch: Patch) extends Guardian with Serializable {
  private val sensors: ConcurrentHashMap[String, SensorEntry] = new ConcurrentHashMap
  private val patchGuardians: ConcurrentHashMap[String, GuardianEntry] = new ConcurrentHashMap
  private val detections: ConcurrentHashMap[String, SensorDetection] = new ConcurrentHashMap

  private val brokenSensors: ConcurrentHashMap[String, Long] = new ConcurrentHashMap
  private val brokenGuardians: ConcurrentHashMap[String, Long] = new ConcurrentHashMap

  private val preAlertGuardians: ConcurrentHashMap[String, Option[Long]] = new ConcurrentHashMap
  private var timer: Timer = _

  private var averageTemp: Option[Double] = None

  private var state: GuardianStateEnum = GuardianStateEnum.IDLE

  @throws[RemoteException]
  override def tell(text: String): Unit = {
    println(text)
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
  override def freshSensorDetections(): Unit = {
    state match {
      case GuardianStateEnum.IDLE =>
        updateSensorsData()
        if (averageTemp.isDefined) {
          if (averageTemp.get > CommonConfig.ALERT_TEMP) {
            changeState(GuardianStateEnum.PRE_ALERT)

            timer = new Timer()
            timer.schedule(new TimerTask {
              override def run(): Unit = {
                if (state == GuardianStateEnum.PRE_ALERT) {
                  if (enoughAlertGuardiansTimeElapsed) {
                    changeState(GuardianStateEnum.ALARM)
                  }
                }

                timer.cancel()
              }
            }, 6000)

          }
        }

      case GuardianStateEnum.PRE_ALERT =>
        updateSensorsData()
        if (averageTemp.isDefined) {
          if (averageTemp.get < CommonConfig.ALERT_TEMP) {
            changeState(GuardianStateEnum.IDLE)
            timer.cancel()
          }
        }

      case GuardianStateEnum.ALARM =>
        timer.cancel()
    }
  }

  @throws[RemoteException]
  override def getGuardiansState: DashboardGuardianState = {
    DashboardGuardianState(id, averageTemp, state, patch)
  }

  @throws[RemoteException]
  override def setPatchGuardians(systemPatchGuardians: ConcurrentHashMap[String, GuardianEntry]): Unit = {
    patchGuardians.putAll(systemPatchGuardians)
  }

  @throws[RemoteException]
  override def notifyNewGuardian(guardian: GuardianEntry): Unit = {
    patchGuardians.put(guardian.getId, guardian)
  }

  @throws[RemoteException]
  override def setState(newState: GuardianStateEnum): Unit = {
    state = newState
  }

  @throws[RemoteException]
  override def getState: GuardianStateEnum = state

  @throws[RemoteException]
  override def notifyNewGuardianState(guardianId: String, message: StateMessage): Unit = {
    message.state match {
      case GuardianStateEnum.IDLE =>
        preAlertGuardians.remove(guardianId)
      case GuardianStateEnum.PRE_ALERT =>
        preAlertGuardians.put(guardianId, message.time)
      case GuardianStateEnum.ALARM =>
        state = GuardianStateEnum.ALARM
        preAlertGuardians.clear()
    }
  }

  private def changeState(state: GuardianStateEnum): Unit = {
    val time = System.currentTimeMillis()
    setState(state)

    patchGuardians.entrySet().forEach(p => {
      val patchGuardian = p.getValue

      try {
        if (patchGuardian.getId != id) {
          if (state == GuardianStateEnum.ALARM)
          p.getValue.getRemoteObject.notifyNewGuardianState(patchGuardian.getId, StateMessage(state, Some(time)))
          eventuallyRemoveFromBrokenGuardians(patchGuardian.getId)
        }
      } catch {
        case _: RemoteException => checkForBrokenGuardian(patchGuardian.getId)
      }
    })

    if (state == GuardianStateEnum.ALARM) {
      preAlertGuardians.clear()
    }
  }

  private def updateSensorsData(): Unit = {
    detections.clear()
    sensors.entrySet().forEach(s => {
      val sensor = s.getValue
      val sensorObj = sensor.getRemoteObject
      try {
        val detection = sensorObj.getDetection

        if (patch.includePoint(detection.position)) {
          detections.put(sensor.getId, detection)
          calculateAverageTemp()
          eventuallyRemoveFromBrokenSensors(sensor.getId)
        }
      } catch {
        case _: RemoteException => checkForBrokenSensor(sensor.getId)
      }
    })
  }

  def enoughAlertGuardiansTimeElapsed: Boolean = {
    var count: Int = 0
    val currentTime = System.currentTimeMillis()
    preAlertGuardians.entrySet().forEach(p => {
      if (p.getValue.isDefined) {
        if ((currentTime - p.getValue.get) > CommonConfig.ALERT_MIN_TIME) {
          count += 1
        }
      } else {
        count += 1
      }
    })

    count + 1 > patchGuardians.size / 2
  }

  private def calculateAverageTemp(): Unit = {
    var sum: Option[Double] = None
    detections.entrySet().forEach(d => {
      val temp = d.getValue.temperature
      if (temp.isDefined) sum = {
        if (sum.isDefined) {
          Some(sum.get + temp.get)
        } else {
          Some(temp.get)
        }
      }
    })

    averageTemp = if (sum.isDefined) Some(sum.get / detections.entrySet().size()) else None
  }

  private def eventuallyRemoveFromBrokenGuardians(guardianId: String) = {
    if (brokenGuardians.contains(guardianId)) brokenGuardians.remove(guardianId)
  }

  private def eventuallyRemoveFromBrokenSensors(sensorId: String) = {
    if (brokenSensors.contains(sensorId)) brokenGuardians.remove(sensorId)
  }

  private def checkForBrokenGuardian(guardianId: String): Unit = {
    val currentTime = System.currentTimeMillis()
    if (brokenGuardians.containsKey(guardianId)) {
      if (currentTime - brokenGuardians.get(guardianId) > Config.MAX_RETRY_TIME) {
        patchGuardians.remove(guardianId)
        brokenGuardians.remove(guardianId)
        preAlertGuardians.remove(guardianId)
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
}
