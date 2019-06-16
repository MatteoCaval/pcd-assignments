package assignment3.e2.akka

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
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


object GuardianActor {
  def props(patch: Patch) = Props(new GuardianActor(UUID.randomUUID().toString, patch))
}

class GuardianActor(val guardianId: String, val patch: Patch) extends Actor with ActorLogging {

  import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
  import context.dispatcher

  private val cluster = Cluster(context.system)
  private val mediator = DistributedPubSub(context.system).mediator

  mediator ! Subscribe(SubSubMessages.PATCH_ALERT, self)
  mediator ! Subscribe(SubSubMessages.TERMINATE_ALERT, self)

  mediator ! Subscribe(SubSubMessages.GUARDIAN_UP, self)
  mediator ! Subscribe(SubSubMessages.SENSOR_DATA, self)


  private var receivedTemperatures: Map[String, Double] = Map()
  private var registeredSensors: Map[ActorRef, String] = Map()

  private var patchGuardians: Map[ActorRef, String] = Map()

  var averageTemperature: Option[Double] = None
  var state: String = GuardianState.OK

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
      log.info("UPPPPPP")
      context.become(sensorInformations)
      context.system.scheduler.scheduleOnce(4 seconds) {
        mediator ! Publish(SubSubMessages.GUARDIAN_UP, GuardianUp(this.patch, this.state))
      }

  }

  def sensorInformations: Receive = {

    case SensorData(sensorId, Some(temperature), position) if patch.includePoint(position) => // check position inside patch
      log.info(s"Received sensor data $temperature from ${sender.path}, position: $position¶")
      receivedTemperatures = receivedTemperatures + (sensorId -> temperature)
      updateAverageTemperature()
      if (!registeredSensors.contains(sender)) {
        registeredSensors += (sender -> sensorId)
        context.watch(sender)
      }


    case SensorData(id, _, position) if registeredSensors.contains(sender) && !patch.includePoint(position) =>
      log.info(s"[Patch${this.patch.id}] Deleting sensor, I have ${registeredSensors.size}")
      registeredSensors -= sender
      receivedTemperatures -= id
      updateAverageTemperature()


    // messaggio inviato da un guardiano quando va su
    case GuardianUp(senderPatch, senderState) if senderPatch == this.patch && sender != self =>
      log.info(s"Guardian ${sender} up")
      patchGuardians += (sender -> senderState)
      sender ! GuardianInfo(guardianId, this.patch, this.state, this.averageTemperature)

    //sensor actor termination
    case Terminated(ref) if registeredSensors.contains(ref) =>
      val terminatedSensorId = registeredSensors(ref)
      log.info(s"Sensor with id $terminatedSensorId and path ${ref.path} terminated")
      registeredSensors -= ref
      receivedTemperatures -= terminatedSensorId
      updateAverageTemperature()


    case Terminated(ref) if patchGuardians.contains(ref) =>
      log.info(s"Guardian ${ref.path} terminated")
      patchGuardians -= ref

    // messaggio inviato dalla dashboard
    case RequestGuardianInformations =>
      log.info(s"Received information request")
      sender ! GuardianInfo(guardianId, patch, state, averageTemperature)

    // ricevuto da un altro guardiano
    case GuardianInfo(_, senderPatch, state, _) if senderPatch == this.patch =>
      if (!patchGuardians.contains(sender)) {
        context.watch(sender)
      }
      patchGuardians += (sender -> state)


  }

  //  def alertMessages: Receive = {
  //    case PathInAlert(alertedPatch) if alertedPatch == this.patch =>
  //      log.info("My patch is in alert")
  //      this.state = GuardianState.ALERT
  //      mediator ! Publish(SubSubMessages.GUARDIAN_INFO, GuardianInfo(guardianId, patch, state, averageTemperature))
  //
  //    case alertedPatch: Patch if alertedPatch == this.patch =>
  //      log.info("Alert terminated")
  //      this.state = GuardianState.OK
  //      mediator ! Publish(SubSubMessages.GUARDIAN_INFO, GuardianInfo(guardianId, patch, state, averageTemperature))
  //
  //  }

  private def updateAverageTemperature(): Unit = {
    val numOfTempValues = receivedTemperatures.size
    averageTemperature = if (numOfTempValues > 0) Some(receivedTemperatures.values.sum / numOfTempValues) else None

    if (averageTemperature.isDefined && averageTemperature.get > Config.MAX_TEMP && state != GuardianState.ALERT) {
      state = GuardianState.PREALERT
    } else {
      state = GuardianState.OK
    }

    log.info(s"Updating temperature (sensors ${registeredSensors.size} and temps ${receivedTemperatures.size}) with: $averageTemperature")
    mediator ! Publish(SubSubMessages.GUARDIAN_INFO, GuardianInfo(guardianId, patch, state, averageTemperature))
  }

}
