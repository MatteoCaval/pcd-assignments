package assignment3.e2.akka

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import assignment3.e2.common.P2d

class DashboardActor extends Actor with ActorLogging {

  private var sensorPosition: Map[String, P2d] = Map()
  private var guardians: Map[String, GuardianInfo] = Map()

  val mediator: ActorRef = DistributedPubSub(context.system).mediator

  mediator ! Subscribe(SubSubMessages.REGISTERED_TEMPERATURE, self)
  mediator ! Subscribe(SubSubMessages.SENSOR_POSITION, self)


  override def receive: Receive = {
    case p: P2d =>
      log.info(s"Received position of sensor ${sender.path}: $p")

    case AverageTemperature(temperature, patch) =>
      log.info(s"Received temperature from ${sender}: temperature $temperature at patch $patch")



  }

}
