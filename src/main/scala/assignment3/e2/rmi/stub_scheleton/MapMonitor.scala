package assignment3.e2.rmi.stub_scheleton

import java.rmi.{Remote, RemoteException}

trait MapMonitor extends Remote {
  @throws[RemoteException]
  def addDashboard(dashboard: DashboardStr): Unit

  @throws[RemoteException]
  def addGuardian(guardian: GuardianStr): Unit

  @throws[RemoteException]
  def addSensor(sensor: SensorStr): Unit
}