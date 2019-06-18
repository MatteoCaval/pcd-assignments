package assignment3.e2.common

trait MapMonitorView {
  def notifySensor(sensorPos: DashboardSensorPosition): Unit

  def notifyGuardian(guardianState: DashboardGuardianState): Unit

  def notifySensorRemoved(sensorId: String)

  def notifyGuardianRemoved(guardianId: String, patchId: Int)

  def notifyAlarmStateEnabled(patchId: Int, enabled: Boolean)
}
