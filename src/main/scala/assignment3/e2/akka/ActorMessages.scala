package assignment3.e2.akka

import akka.actor.ActorRef
import assignment3.e2.common.{P2d, Patch}

object ActorMessages {

  case class GuardianInfo(guardianId: String, patch: Patch, state: String, temperature: Option[Double])

  // message send by guardian on start to nofity all guardians (also the ones in his patch) about his presence
  case class GuardianUp(patch: Patch, state: String)

  case object RequestGuardianInformations

  case class PatchAlarmEnabled(patchId: Int, enabled:Boolean)

  case class GuardianStateMesssage(state: String, alertTime: Option[Long])


  case class SensorData(sensorId: String, temperature: Option[Double], position: P2d)

  case object RegistrateSensor

  case object RegistrateSensorWithCompleteData

  case class SensorRegistrationData(sensorId: String, ref: ActorRef)

  case class SensorRegistrationCompleteData(sensorId: String, ref: ActorRef, temperature: Option[Double], position: P2d)


}
