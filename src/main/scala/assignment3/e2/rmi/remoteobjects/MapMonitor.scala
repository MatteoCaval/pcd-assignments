package assignment3.e2.rmi.remoteobjects

import java.rmi.{Remote, RemoteException}

import assignment3.e2.rmi.mapentry.{DashboardStr, GuardianStr, SensorStr}

trait MapMonitor extends Remote {
  @throws[RemoteException]
  def addDashboard(dashboard: DashboardStr): Unit

  @throws[RemoteException]
  def addGuardian(guardian: GuardianStr): Unit

  @throws[RemoteException]
  def addSensor(sensor: SensorStr): Unit
}