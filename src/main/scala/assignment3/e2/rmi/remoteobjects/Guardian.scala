package assignment3.e2.rmi.remoteobjects

import java.rmi.{Remote, RemoteException}
import java.util.concurrent.ConcurrentHashMap

import assignment3.e2.common.GuardianStateEnum.GuardianStateEnum
import assignment3.e2.common.{DashboardGuardianState, StateMessage}
import assignment3.e2.rmi.mapentry.{GuardianEntry, SensorEntry}

trait Guardian extends Remote {

  @throws[RemoteException]
  def notifyNewGuardianState(id: String, message: StateMessage)

  @throws[RemoteException]
  def getGuardiansState: DashboardGuardianState

  @throws[RemoteException]
  def setPatchGuardians(stringToGuardian: ConcurrentHashMap[String, GuardianEntry]): Unit

  @throws[RemoteException]
  def notifyNewGuardian(guardian: GuardianEntry)

  @throws[RemoteException]
  def tell(text: String): Unit

  @throws[RemoteException]
  def notifyNewSensor(sensor: SensorEntry): Unit

  @throws[RemoteException]
  def setSensors(sensors: ConcurrentHashMap[String, SensorEntry]): Unit

  @throws[RemoteException]
  def setState(state: GuardianStateEnum)

  @throws[RemoteException]
  def getState: GuardianStateEnum

  @throws[RemoteException]
  def freshSensorDetections(): Unit
}