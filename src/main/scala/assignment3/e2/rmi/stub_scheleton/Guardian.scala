package assignment3.e2.rmi.stub_scheleton

import java.rmi.{Remote, RemoteException}
import java.util.concurrent.ConcurrentHashMap

import assignment3.e2.common.GuardianStateEnum.GuardianStateEnum
import assignment3.e2.common.{DashboardGuardianState, StateMessage}

trait Guardian extends Remote {

  @throws[RemoteException]
  def notifyNewGuardianState(id: String, message: StateMessage)

  @throws[RemoteException]
  def getGuardiansStatus: DashboardGuardianState

  @throws[RemoteException]
  def setPatchGuardians(stringToGuardian: ConcurrentHashMap[String, GuardianStr]): Unit

  @throws[RemoteException]
  def notifyNewGuardian(guardian: GuardianStr)

  @throws[RemoteException]
  def tell(text: String): Unit

  @throws[RemoteException]
  def notifyNewSensor(sensor: SensorStr): Unit

  @throws[RemoteException]
  def setSensors(sensors: ConcurrentHashMap[String, SensorStr]): Unit

  @throws[RemoteException]
  def setState(state: GuardianStateEnum)

  @throws[RemoteException]
  def getState: GuardianStateEnum

  @throws[RemoteException]
  def freshSensorDetections(): Unit
}