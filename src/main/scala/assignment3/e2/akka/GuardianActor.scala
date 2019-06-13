package assignment3.e2.akka

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
import assignment3.e2.common.Patch

object GuardianState {
  val OK = "ok"
  val PREALERT = "prealert"
  val ALERT = "alert"
}

case class GuardianInfo(guardianId: String, patch: Patch, state: String, temperature: Double)

case object RegistrateGuardian


object GuardianActor {
  def props(patch: Patch) = Props(new GuardianActor(UUID.randomUUID().toString, patch))
}

class GuardianActor(val guardianId: String, val patch: Patch) extends Actor with ActorLogging {

  import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe

  private val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe(SubSubMessages.TEMPERATURE, self) // subscribe to topic "content"
  mediator ! Subscribe(SubSubMessages.PATCH_ALERT, self) // subscribe to topic "content"
  mediator ! Subscribe(SubSubMessages.TERMINATE_ALERT, self)

  private val cluster = Cluster(context.system)

  private var receivedTemperatures: Map[String, Double] = Map()
  private var registeredSensors: Map[ActorRef, String] = Map()

  var averageTemperature: Double = Double.NaN
  var state: String = GuardianState.OK

  override def preStart(): Unit = {
    cluster.subscribe(
      self,
      initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent]
    )
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = sensorInformations.orElse(informationRequest).orElse(alertMessages)

  def sensorInformations: Receive = {
    case RegisteredTemperature(sensorId, temperature, position) => // check position inside patch
      log.info(s"Received temperature $temperature from ${sender.path}")
      receivedTemperatures = receivedTemperatures + (sender.path.toString -> temperature)
      updateAverageTemperature()
      if (!registeredSensors.contains(sender)) {
        registeredSensors += (sender -> sensorId)
        context.watch(sender)
      }

    // ha senso se osservo anche i ref?
    //    case MemberRemoved(member, previousStatus) if member.hasRole("sensor") =>
    //      log.info(s"Sensor ${member.address} removed")
    //      if (receivedTemperatures.contains(member.address.toString)) {
    //        receivedTemperatures = receivedTemperatures - member.address.toString
    //        updateAverageTemperature()
    //      }

    case Terminated(ref) =>
      val terminatedSensorId = registeredSensors(ref)
      log.info(s"Sensor with id $terminatedSensorId and path ${ref.path} terminated")
      registeredSensors -= ref
      receivedTemperatures -= terminatedSensorId
      updateAverageTemperature()

    case RegistrateGuardian =>
      log.info(s"Received guardian registration request from ${sender.path}")
      sender ! GuardianInfo(guardianId, patch, state, averageTemperature)

  }


  def informationRequest: Receive = {
    case RequestGuardianInformations =>
      log.info(s"Received information request")
      sender ! GuardianInfo(guardianId, patch, state, averageTemperature)
  }

  def alertMessages: Receive = {
    case PathInAlert(alertedPatch) if alertedPatch == this.patch =>
      log.info("My patch is in alert")
      this.state = GuardianState.ALERT
      mediator ! Publish(SubSubMessages.GUARDIAN_INFO, GuardianInfo(guardianId, patch, state, averageTemperature))

    case alertedPatch: Patch if alertedPatch == this.patch =>
      log.info("Alert terminated")
      this.state = GuardianState.OK
      mediator ! Publish(SubSubMessages.GUARDIAN_INFO, GuardianInfo(guardianId, patch, state, averageTemperature))

  }


  private def updateAverageTemperature(): Unit = {
    averageTemperature = receivedTemperatures.values.sum / receivedTemperatures.values.size

    if (averageTemperature > Config.MAX_TEMP && state != GuardianState.ALERT) {
      state = GuardianState.PREALERT
    } else {
      state = GuardianState.OK
    }
    log.info(s"Updating temperature: $averageTemperature")
    mediator ! Publish(SubSubMessages.GUARDIAN_INFO, GuardianInfo(guardianId, patch, state, averageTemperature))
  }


}
