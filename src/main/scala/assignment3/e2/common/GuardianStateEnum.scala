package assignment3.e2.common

object GuardianStateEnum extends Enumeration {

  type GuardianStateEnum = Value
  val IDLE, BAD_TEMP_DETECTED, PRE_ALERT, ALERT = Value

  override def toString(): String = {
    this match {
      case GuardianStateEnum.IDLE => "Idle"
      case GuardianStateEnum.BAD_TEMP_DETECTED => "High average"
      case GuardianStateEnum.ALERT => "Alert"
      case GuardianStateEnum.PRE_ALERT => "Pre-alert"
    }
  }
}
