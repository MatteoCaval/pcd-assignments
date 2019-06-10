package assignment3.e2.akka

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberRemoved, MemberUp, UnreachableMember}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
import assignment3.e2.common.Patch

sealed class GuardianState

case object Normal extends GuardianState

case object PreAlert extends GuardianState

case object Alert extends GuardianState

case class GuardianInfo(patch: Patch, state: String)

case class AverageTemperature(temperature: Double, patch: Patch)

object GuardianActor {
  private val MAX_TEMP: Double = 6.0

  def props(patch: Patch) = Props(new GuardianActor(patch))
}

class GuardianActor(patch: Patch) extends Actor with ActorLogging {

  import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, SubscribeAck}

  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe(SubSubMessages.TEMPERATURE, self) // subscribe to topic "content"


  val cluster = Cluster(context.system)

  var receivedTemperatures: Map[String, Double] = Map()
  var averageTemperature: Double = Double.NaN


  override def preStart(): Unit = {
    cluster.subscribe(
      self,
      initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent],
      classOf[UnreachableMember]
    )
  }

  override def postStop(): Unit = cluster.unsubscribe(self)


  override def receive: Receive = clusterEvents.orElse(informationRequest)

  def clusterEvents: Receive = {
    case RegisteredTemperature(sensorId, temperature) =>
      log.info(s"Received temperature $temperature from ${sender.path}")
      receivedTemperatures = receivedTemperatures + (sender.path.toString -> temperature)
      updateAverageTemperature()
      mediator ! Publish(SubSubMessages.REGISTERED_TEMPERATURE, AverageTemperature(averageTemperature, patch))


    case MemberUp(member) =>
      log.info(s"new member up: ${member}")

    case MemberRemoved(member, previousStatus) =>
      log.info(s"member removed: ${member.address} previous status: $previousStatus")

    case o: MemberEvent =>
      log.info(s"Member Event: $o")

  }


  def informationRequest: Receive = {
    case RequestGuardianInformations =>
      log.info(s"Received infomation request")
      sender ! GuardianInfo(patch, "ok")
  }


  private def updateAverageTemperature() =
    averageTemperature = receivedTemperatures.values.sum / receivedTemperatures.values.size

}
