package assignment3.e2.akka

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.pubsub.DistributedPubSub
import assignment3.e2.common.{P2d, PatchManager}

import scala.concurrent.duration._
import scala.util.Random

case class SensorData(sensorId: String, temperature: Option[Double], position: P2d)

case object RegistrateSensor

case object RegistrateSensorWithCompleteData

case class SensorRegistrationData(sensorId: String, ref: ActorRef)

case class SensorRegistrationCompleteData(sensorId: String, ref: ActorRef, temperature: Option[Double], position: P2d)


object SensorActor {
  def props(initialPoint: P2d) = Props(new SensorActor(UUID.randomUUID().toString, initialPoint))
}

class SensorActor(val sensorId: String, var position: P2d) extends Actor with ActorLogging {

  import akka.cluster.pubsub.DistributedPubSubMediator.Publish

  private val mediator = DistributedPubSub(context.system).mediator

  private var registeredTemperature: Option[Double] = None

  import context.dispatcher

  override def preStart(): Unit = {
    context.system.scheduler.schedule(10 second, 3 second) {
      randomBehaviour()
    }

  }

  override def receive: Receive = {

    case RegistrateSensor =>
      log.info(s"Received registration data from $sender")
      sender() ! SensorData(sensorId, registeredTemperature, position)

    case RegistrateSensorWithCompleteData =>
      log.info(s"Received registration data from $sender")
      sender() ! SensorRegistrationCompleteData(sensorId, self, registeredTemperature, position)

    case m =>
      log.info(s"Boh, received $m")
  }


  private def randomBehaviour(): Unit = {
    val randomNum = Random.nextInt(5)
    if (randomNum == 4) {
      updateRandomPosition()

    } else if (randomNum > 2) {
      updateRandomTemperature()

    }
  }


  private def updateRandomTemperature(): Unit = {
    this.registeredTemperature = Some(Random.nextDouble() * 10)
    log.info(s"Updating sensor temperature to $registeredTemperature")
    mediator ! Publish(PubSubMessages.SENSOR_DATA, SensorData(sensorId, registeredTemperature, position))
  }

  private def updateRandomPosition(): Unit = {
    this.position = PatchManager.getRandomPositionInsideMap
    log.info(s"Updating sensor position to $position")
    mediator ! Publish(PubSubMessages.SENSOR_DATA, SensorData(sensorId, this.registeredTemperature, position))
  }


}

