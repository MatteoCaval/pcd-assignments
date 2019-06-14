package assignment3.e2.common

trait MapMonitorView {
  def notifySensor(sensorPos: DashboardSensorPosition): Unit

  def notifyGuardian(guardianState: DashboardGuardianState): Unit
}
