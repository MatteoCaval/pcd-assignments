package assignment3.e2.akka

import java.util.{Date, UUID}

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props, Terminated}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberUp}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
import assignment3.e2.common.Patch

import scala.concurrent.duration._

object GuardianState {
  val OK = "ok"
  val PREALERT = "prealert"
  val ALERT = "alert"
}

case class GuardianInfo(guardianId: String, patch: Patch, state: String, temperature: Option[Double])

case class GuardianUp(patch: Patch, state: String)

case object RequestGuardianInformations

case class PathInAlert(patch: Patch)

case class PatchReleaseMessage(patchId: Int)

case class GuardianStateMesssage(state: String, alertTime: Option[Long])


object GuardianActor {
  def props(patch: Patch) = Props(new GuardianActor(UUID.randomUUID().toString, patch))
}

class GuardianActor(val guardianId: String, val patch: Patch) extends Actor with ActorLogging {

  import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
  import context.dispatcher

  private val cluster = Cluster(context.system)
  private val mediator = DistributedPubSub(context.system).mediator

  mediator ! Subscribe(SubSubMessages.TERMINATE_ALERT, self)
  mediator ! Subscribe(SubSubMessages.GUARDIAN_UP, self)
  mediator ! Subscribe(SubSubMessages.SENSOR_DATA, self)


  private var receivedTemperatures: Map[String, Double] = Map()
  private var registeredSensors: Map[ActorRef, String] = Map()

  private var patchGuardians: Map[ActorRef, String] = Map()

  private var alertedGuardian: Map[ActorRef, Option[Long]] = Map()

  var averageTemperature: Option[Double] = None
  var state: String = GuardianState.OK

  var patchAlertTimer: Cancellable = null

  override def preStart(): Unit = {
    cluster.subscribe(
      self,
      initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent]
    )
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberUp(_) =>
      context.become(sensorInformations.orElse(guardianInformations))
      context.system.scheduler.scheduleOnce(4 seconds) {
        mediator ! Publish(SubSubMessages.GUARDIAN_UP, GuardianUp(this.patch, this.state))
      }
  }

  def sensorInformations: Receive = {

    // data of sensor inside my patch
    case SensorData(sensorId, Some(temperature), position) if patch.includePoint(position) =>
      log.info(s"Received sensor data $temperature from ${sender.path}, position: $position")
      receivedTemperatures = receivedTemperatures + (sensorId -> temperature)
      updateAverageTemperature()
      if (!registeredSensors.contains(sender)) {
        registeredSensors += (sender -> sensorId)
        context.watch(sender)
      }

    // data of sensor that was inside my patch
    case SensorData(id, _, position) if registeredSensors.contains(sender) && !patch.includePoint(position) =>
      log.info(s"[Patch${this.patch.id}] Deleting sensor, I have ${registeredSensors.size}")
      registeredSensors -= sender
      receivedTemperatures -= id
      updateAverageTemperature()

    // messaggio inviato da un guardiano quando va su
    case GuardianUp(senderPatch, senderState) if senderPatch == this.patch && sender != self =>
      log.info(s"Guardian ${sender} up")
      if (senderState == GuardianState.PREALERT && !alertedGuardian.contains(sender)) {
        alertedGuardian += (sender -> None)
      }

      patchGuardians += (sender -> senderState)
      sender ! GuardianInfo(guardianId, this.patch, this.state, this.averageTemperature)

    //sensor actor termination
    case Terminated(ref) if registeredSensors.contains(ref) =>
      val terminatedSensorId = registeredSensors(ref)
      log.info(s"Sensor with id $terminatedSensorId and path ${ref.path} terminated")
      registeredSensors -= ref
      receivedTemperatures -= terminatedSensorId
      updateAverageTemperature()

  }

  def guardianInformations: Receive = {

    // ricevuto da un altro guardiano
    case GuardianInfo(_, senderPatch, senderState, _) if senderPatch == this.patch =>
      if (!patchGuardians.contains(sender)) {
        context.watch(sender)
      }
      patchGuardians += (sender -> senderState)

    // guardian termination
    case Terminated(ref) if patchGuardians.contains(ref) =>
      log.info(s"Guardian ${ref.path} terminated")
      patchGuardians -= ref

    // messaggio inviato dalla dashboard
    case RequestGuardianInformations =>
      log.info(s"Received information request")
      sender ! GuardianInfo(guardianId, patch, state, averageTemperature)

    // guardian state, send by other guardians on state changes
    case GuardianStateMesssage(senderState, time) =>
      senderState match {
        case GuardianState.ALERT => //switch my state to alert and notify dashboards
          log.info("guardian of my patch in alert, now me")
          this.state = GuardianState.ALERT
          this.alertedGuardian = Map()
          mediator ! Publish(SubSubMessages.PATCH_ALERT, PathInAlert(this.patch))

        case GuardianState.PREALERT =>
          this.alertedGuardian += (sender -> time)
          log.info(s"Guardian of my patch in prealert, now ${alertedGuardian.size}/${patchGuardians.size}")

        case GuardianState.OK =>
          this.alertedGuardian -= sender //removes sender from alerted guardians
      }

    case PatchReleaseMessage(patchId) if this.patch.id == patchId =>
      this.alertedGuardian = Map()
      this.state = GuardianState.OK
      this.broadcastGuardianInfos()


  }


  private def updateAverageTemperature(): Unit = {
    val numOfTempValues = receivedTemperatures.size
    averageTemperature = if (numOfTempValues > 0) Some(receivedTemperatures.values.sum / numOfTempValues) else None
    this.handleState(averageTemperature)
    this.broadcastGuardianInfos()
  }


  private def handleState(temperature: Option[Double]) = temperature match {
    case Some(temp) if temp > Config.MAX_TEMP && state == GuardianState.OK =>
      log.info("PREALERT")
      state = GuardianState.PREALERT
      this.notifyStateToPatchGuardians()
      this.checkAlertState()

    case Some(temp) if temp <= Config.MAX_TEMP && state == GuardianState.PREALERT =>
      log.info("IDLE")
      state = GuardianState.OK
      this.notifyStateToPatchGuardians()
      if (this.patchAlertTimer != null) {
        this.patchAlertTimer.cancel()
      }

    case None if state == GuardianState.PREALERT => // not very sure
      state = GuardianState.OK
      this.notifyStateToPatchGuardians()

    case _ =>

  }

  private def broadcastGuardianInfos(): Unit =
    mediator ! Publish(SubSubMessages.GUARDIAN_INFO, GuardianInfo(guardianId, patch, state, averageTemperature))

  /**
    * send state to all patch guardians
    */
  private def notifyStateToPatchGuardians(): Unit = {
    val time: Option[Long] = if (this.state == GuardianState.PREALERT) Some(System.currentTimeMillis()) else None
    patchGuardians.keySet.foreach(_ ! GuardianStateMesssage(this.state, time))
  }

  private def checkAlertState(): Unit = {
    log.info(s"checking alert, my state is $state, alerted: ${alertedGuardian.size}, total: ${patchGuardians.size}, mayority $isMajorityOfGuardiansInPreAlert")
    //    if (this.state == GuardianState.PREALERT && isMajorityOfGuardiansInPreAlert) {
    log.info("Majority in alert")
    this.patchAlertTimer = context.system.scheduler.scheduleOnce(Config.ALERT_MIN_TIME millis) {
      log.info("sec elapsed")
      if (isMajorityOfGuardiansInPreAlertWithElapsedTime) {
        log.info(s"Patch ${patch.id} in alert")
        mediator ! Publish(SubSubMessages.PATCH_ALERT, PathInAlert(this.patch))
        this.notifyStateToPatchGuardians()
        this.alertedGuardian = Map()
      }
    }
    //    }
  }

  private def isMajorityOfGuardiansInPreAlert = {
    (this.alertedGuardian.size + 1) / (this.patchGuardians.size + 1) > 0.5
  }

  private def isMajorityOfGuardiansInPreAlertWithElapsedTime: Boolean =
    (this.getNumberOfGuardiansWithElapsedTime + 1) / (this.patchGuardians.size + 1) > 0.5

  private def getNumberOfGuardiansWithElapsedTime: Int = {
    val currentTime = System.currentTimeMillis()
    log.info(s"alerted: ${this.alertedGuardian.toString()}")
    this.alertedGuardian.count(g => g._2.isEmpty || currentTime - g._2.get > Config.ALERT_MIN_TIME)
  }


}
