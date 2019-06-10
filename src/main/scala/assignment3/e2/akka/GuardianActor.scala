package assignment3.e2.akka

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberRemoved, MemberUp, UnreachableMember}
import akka.cluster.pubsub.DistributedPubSub
import assignment3.e2.common.Patch

object GuardianActor {
  private val MAX_TEMP: Double = 6.0

  def props(patch: Patch) = Props(new GuardianActor(patch))
}

class GuardianActor(patch: Patch) extends Actor with ActorLogging {

  import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, SubscribeAck}

  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe("temperature", self) // subscribe to topic "content"


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


  override def receive: Receive = {
    case RegisteredTemperature(sensorId, temperature) =>
      log.info(s"Received temperature $temperature from ${sender.path}")
      receivedTemperatures = receivedTemperatures + (sender.path.toString -> temperature)


    case MemberUp(member) =>
      log.info(s"new member up: ${member}")

    case MemberRemoved(member, previousStatus) =>
      log.info(s"member removed: ${member.address} previous status: $previousStatus")

    case o: MemberEvent =>
      log.info(s"Member Event: $o")

  }


  private def updateAverageTemperature() =
    averageTemperature = receivedTemperatures.values.sum / receivedTemperatures.values.size

}
