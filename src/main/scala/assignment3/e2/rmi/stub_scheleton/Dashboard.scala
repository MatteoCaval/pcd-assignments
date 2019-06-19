package assignment3.e2.rmi.stub_scheleton

import java.rmi.{Remote, RemoteException}
import java.util.concurrent.ConcurrentHashMap

trait Dashboard extends Remote {

  @throws[RemoteException]
  def notifyAlarmOff(patchId: Int): Unit

  @throws[RemoteException]
  def update(): Unit

  @throws[RemoteException]
  def tell(text: String): Unit

  @throws[RemoteException]
  def notifyNewGuardian(guardian: GuardianStr): Unit

  @throws[RemoteException]
  def notifyNewSensor(sensor: SensorStr): Unit

  @throws[RemoteException]
  def setSensors(clone: ConcurrentHashMap[String, SensorStr]): Unit

  @throws[RemoteException]
  def setGuardians(clone: ConcurrentHashMap[String, GuardianStr]): Unit
}