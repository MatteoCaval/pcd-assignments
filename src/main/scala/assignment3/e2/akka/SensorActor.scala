package assignment3.e2.akka

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.pubsub.DistributedPubSub
import assignment3.e2.common.P2d

import scala.concurrent.duration._

case class RegisteredTemperature(sensorId: String, temperature: Double)
case object RegistrateSensor
case class SensorRegistrationData(sensorId:String, ref: ActorRef)

object SensorActor {
  def props(initialPoint: P2d) = Props(new SensorActor(UUID.randomUUID().toString, initialPoint))
}

class SensorActor(sensorId: String, position: P2d) extends Actor with ActorLogging {

  import akka.cluster.pubsub.DistributedPubSubMediator.Publish

  val mediator = DistributedPubSub(context.system).mediator

  import context.dispatcher

  override def preStart(): Unit = {
    context.system.scheduler.scheduleOnce(5 second) {
      self ! "temperature"
    }

  }

  override def receive: Receive = {
    case "temperature" =>
      log.info(s"[$self]Received temp command")
      mediator ! Publish(SubSubMessages.TEMPERATURE, RegisteredTemperature(sensorId, 5.6))
      mediator ! Publish(SubSubMessages.SENSOR_POSITION, position)

    case RegistrateSensor =>
      sender() ! SensorRegistrationData(sensorId, self)

    case m =>
      log.info(s"Boh, received $m")
  }

}
