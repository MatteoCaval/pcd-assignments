package assignment3.e2.akka

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberRemoved, UnreachableMember}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import assignment3.e2.common.P2d

class DashboardActor extends Actor with ActorLogging {

  private var sensorPosition: Map[String, P2d] = Map()
  private var guardians: Map[String, GuardianInfo] = Map()

  val cluster = Cluster(context.system)

  val mediator: ActorRef = DistributedPubSub(context.system).mediator

  mediator ! Subscribe(SubSubMessages.GUARDIAN_INFO, self)
  mediator ! Subscribe(SubSubMessages.SENSOR_POSITION, self)


  override def preStart(): Unit = {
    cluster.subscribe(
      self,
      initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent]
    )
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case p: P2d =>
      log.info(s"Received position of sensor ${sender.path}: $p")

    case info@GuardianInfo(patch, state, temperature) =>
      log.info(s"Received temperature from ${sender.path}: temperature $temperature at patch $patch")
      guardians = guardians + (sender.path.toString -> info)

    //    case GuardianRemoved(address) =>
    //      log.info(s"Guardian $address removed")
    //      if (guardians.contains(address)) {
    //        guardians = guardians - address
    //      }

    case MemberRemoved(member, previousStatus) if member.hasRole("guardian") =>
      log.info(s"Guardian ${member.address} removed")

    case MemberRemoved(member, previousStatus) if member.hasRole("sensor") =>
      log.info(s"Sensor ${member.address} removed")


  }

}
