package assignment3.e2.common

object GuardianStateEnum extends Enumeration {
  type GuardianStateEnum = Value
  val IDLE: GuardianStateEnum.Value = Value("Idle")
  val PRE_ALERT: GuardianStateEnum.Value = Value("Pre-alert")
  val ALARM: GuardianStateEnum.Value = Value("Alarm")
}
