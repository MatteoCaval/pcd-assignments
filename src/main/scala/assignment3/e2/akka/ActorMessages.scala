package assignment3.e2.akka

import akka.actor.ActorRef
import assignment3.e2.common.GuardianStateEnum.GuardianStateEnum
import assignment3.e2.common.{GuardianStateEnum, P2d, Patch}

object ActorMessages {

  case class GuardianInfo(guardianId: String, patch: Patch, state: GuardianStateEnum, temperature: Option[Double])

  // message send by guardian on start to nofity all guardians (also the ones in his patch) about his presence
  case class GuardianUp(patch: Patch, state: GuardianStateEnum)

  case object RequestGuardianInformations

  case class PatchAlarmEnabled(patchId: Int, enabled: Boolean)

  case class GuardianStateMesssage(state: GuardianStateEnum, alertTime: Option[Long])

  case class SensorData(sensorId: String, temperature: Option[Double], position: P2d)

  case object RegistrateSensor


}
