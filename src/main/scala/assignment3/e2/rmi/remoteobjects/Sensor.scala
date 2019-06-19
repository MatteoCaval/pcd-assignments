
package assignment3.e2.rmi.remoteobjects

import java.rmi.{Remote, RemoteException}

import assignment3.e2.common.{DashboardSensorPosition, SensorDetection}

trait Sensor extends Remote {
  @throws[RemoteException]
  def getDashboardPosition: DashboardSensorPosition

  @throws[RemoteException]
  def getDetection: SensorDetection

  @throws[RemoteException]
  def tell(text: String): Unit
}