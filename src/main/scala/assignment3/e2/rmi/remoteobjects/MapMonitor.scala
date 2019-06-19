package assignment3.e2.rmi.remoteobjects

import java.rmi.{Remote, RemoteException}

import assignment3.e2.rmi.mapentry.{DashboardEntry, GuardianEntry, SensorEntry}

trait MapMonitor extends Remote {
  @throws[RemoteException]
  def addDashboard(dashboard: DashboardEntry): Unit

  @throws[RemoteException]
  def addGuardian(guardian: GuardianEntry): Unit

  @throws[RemoteException]
  def addSensor(sensor: SensorEntry): Unit
}