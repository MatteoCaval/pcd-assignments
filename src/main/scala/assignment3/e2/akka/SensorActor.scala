package assignment3.e2.akka

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.pubsub.DistributedPubSub
import assignment3.e2.common.P2d

import scala.concurrent.duration._

case class RegisteredTemperature(sensorId: String, temperature: Double, position: P2d)

case class SensorPosition(sensorId: String, position: P2d)

case object RegistrateSensor

case object RegistrateSensorWithCompleteData

case class SensorRegistrationData(sensorId: String, ref: ActorRef)

case class SensorRegistrationCompleteData(sensorId: String, ref: ActorRef, temperature: Option[Double], position: P2d)


object SensorActor {
  def props(initialPoint: P2d) = Props(new SensorActor(UUID.randomUUID().toString, initialPoint))
}

class SensorActor(sensorId: String, position: P2d) extends Actor with ActorLogging {

  import akka.cluster.pubsub.DistributedPubSubMediator.Publish

  private val mediator = DistributedPubSub(context.system).mediator

  private val registeredTemperature: Option[Double] = None

  import context.dispatcher

  override def preStart(): Unit = {
    //    context.system.scheduler.schedule(5 second, 10 second) {
    //      self ! "temperature"
    //    }

  }

  override def receive: Receive = {
    case "temperature" =>
      log.info(s"Received temp command")
      mediator ! Publish(SubSubMessages.TEMPERATURE, RegisteredTemperature(sensorId, 5.6, position))
      mediator ! Publish(SubSubMessages.SENSOR_POSITION, SensorPosition(sensorId, position))

    case RegistrateSensor =>
      log.info(s"Received registration data from ${sender}")
      sender() ! SensorPosition(sensorId, position)

    case RegistrateSensorWithCompleteData =>
      log.info(s"Received registration data from ${sender}")
      sender() ! SensorRegistrationCompleteData(sensorId, self, registeredTemperature, position)

    case m =>
      log.info(s"Boh, received $m")
  }

}
