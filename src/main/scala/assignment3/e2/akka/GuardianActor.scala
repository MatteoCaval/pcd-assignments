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

  override def receive: Receive = sensorInformations.orElse(informationRequest)

  def sensorInformations: Receive = {
    case RegisteredTemperature(sensorId, temperature) =>
      log.info(s"Received temperature $temperature from ${sender.path}")
      receivedTemperatures = receivedTemperatures + (sender.path.toString -> temperature)
      updateAverageTemperature()

      if (averageTemperature > Config.MAX_TEMP) {
        state = GuardianState.PREALERT
      } else {
        state = GuardianState.OK
      }

      mediator ! Publish(SubSubMessages.GUARDIAN_INFO, GuardianInfo(patch, state, averageTemperature))


    case MemberRemoved(member, previousStatus) if member.hasRole("sensor") =>
      log.info(s"Sensor ${member.address} removed")
  }


  def informationRequest: Receive = {
    case RequestGuardianInformations =>
      log.info(s"Received information request")
      sender ! GuardianInfo(patch, state, averageTemperature)
  }


  private def updateAverageTemperature() =
    averageTemperature = receivedTemperatures.values.sum / receivedTemperatures.values.size

}
