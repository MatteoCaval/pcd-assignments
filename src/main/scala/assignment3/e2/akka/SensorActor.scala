package assignment3.e2.akka

import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberRemoved, MemberUp}
import akka.cluster.pubsub.DistributedPubSub

case class RegisteredTemperature(sensorId: String, temperature: Double)


object SensorActor {
  def props(initialPoint: P2d) = Props(new SensorActor(UUID.randomUUID().toString, initialPoint))
}

class SensorActor(sensorId: String, initialPosition: P2d) extends Actor with ActorLogging {

  //  val cluster = Cluster(context.system)

  import akka.cluster.pubsub.DistributedPubSubMediator.{Publish}

  val mediator = DistributedPubSub(context.system).mediator

  //  override def preStart(): Unit = {
  //    cluster.subscribe(
  //      self,
  //      initialStateMode = InitialStateAsEvents,
  //      classOf[MemberEvent]
  //    )
  //  }

  override def receive: Receive = {
    case "temperature" =>
      log.info(s"[$self]Received temp command")
      mediator ! Publish("temperature", RegisteredTemperature(sensorId, 5.6))

    case m =>
      log.info(s"Boh, received $m")
  }

}
