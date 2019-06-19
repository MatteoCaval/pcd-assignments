package assignment3.e2.rmi.remoteobjects

import java.rmi.{Remote, RemoteException}
import java.util.concurrent.ConcurrentHashMap

import assignment3.e2.rmi.mapentry.{GuardianEntry, SensorEntry}

trait Dashboard extends Remote {

  @throws[RemoteException]
  def notifyAlarmOff(patchId: Int): Unit

  @throws[RemoteException]
  def update(): Unit

  @throws[RemoteException]
  def tell(text: String): Unit

  @throws[RemoteException]
  def notifyNewGuardian(guardian: GuardianEntry): Unit

  @throws[RemoteException]
  def notifyNewSensor(sensor: SensorEntry): Unit

  @throws[RemoteException]
  def setSensors(clone: ConcurrentHashMap[String, SensorEntry]): Unit

  @throws[RemoteException]
  def setGuardians(clone: ConcurrentHashMap[String, GuardianEntry]): Unit
}