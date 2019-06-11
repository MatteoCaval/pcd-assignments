package assignment3.e2.akka

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberRemoved}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
import assignment3.e2.common.Patch

object GuardianState {
  val OK = "ok"
  val PREALERT = "prealert"
  val ALERT = "alert"
}

case class GuardianInfo(patch: Patch, state: String, temperature: Double)

object GuardianActor {
  def props(patch: Patch) = Props(new GuardianActor(patch))
}

class GuardianActor(patch: Patch) extends Actor with ActorLogging {

  import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe

  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe(SubSubMessages.TEMPERATURE, self) // subscribe to topic "content"
  mediator ! Subscribe(SubSubMessages.PATCH_ALERT, self) // subscribe to topic "content"
  mediator ! Subscribe(SubSubMessages.TERMINATE_ALERT, self)

  val cluster = Cluster(context.system)

  var receivedTemperatures: Map[String, Double] = Map()
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
    case RegisteredTemperature(sensorId, temperature) =>
      log.info(s"Received temperature $temperature from ${sender.path.address}")
      receivedTemperatures = receivedTemperatures + (sender.path.toString -> temperature)
      updateAverageTemperature()


    case MemberRemoved(member, previousStatus) if member.hasRole("sensor") =>
      log.info(s"Sensor ${member.address} removed")
      if (receivedTemperatures.contains(member.address.toString)) {
        receivedTemperatures = receivedTemperatures - member.address.toString
        updateAverageTemperature()
      }
  }


  def informationRequest: Receive = {
    case RequestGuardianInformations =>
      log.info(s"Received information request")
      sender ! GuardianInfo(patch, state, averageTemperature)
  }

  def alertMessages: Receive = {
    case PathInAlert(alertedPatch) if alertedPatch == this.patch =>
      log.info("My patch is in alert")
      this.state = GuardianState.ALERT
      mediator ! Publish(SubSubMessages.GUARDIAN_INFO, GuardianInfo(patch, state, averageTemperature))

    case alertedPatch: Patch if alertedPatch == this.patch =>
      log.info("Alert terminated")
      this.state = GuardianState.OK
      mediator ! Publish(SubSubMessages.GUARDIAN_INFO, GuardianInfo(patch, state, averageTemperature))

  }


  private def updateAverageTemperature(): Unit = {
    averageTemperature = receivedTemperatures.values.sum / receivedTemperatures.values.size

    if (averageTemperature > Config.MAX_TEMP && state != GuardianState.ALERT) {
      state = GuardianState.PREALERT
    } else {
      state = GuardianState.OK
    }

    mediator ! Publish(SubSubMessages.GUARDIAN_INFO, GuardianInfo(patch, state, averageTemperature))
  }

}
