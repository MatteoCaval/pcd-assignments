package assignment3.e2.akka

import akka.actor.{Actor, ActorLogging, ActorRef, Address}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import assignment3.e2.common.Patch

import scala.concurrent.Future
import scala.concurrent.duration._

case object RequestGuardianInformations

case class GuardianRemoved(address: String)

class GuardianListenerActor extends Actor with ActorLogging {

  import context.dispatcher

  implicit val timeout: Timeout = Timeout(3 seconds)
  val cluster: Cluster = Cluster(context.system)

  var guardians: Map[Address, GuardianInfo] = Map()

  val mediator: ActorRef = DistributedPubSub(context.system).mediator
  mediator ! Subscribe(SubSubMessages.GUARDIAN_INFO, self)

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
    case MemberUp(member) if member.hasRole("guardian") =>
      val actorselection = context.actorSelection(s"${member.address}/user/guardian")
      log.info(s"member up: ${member.address}")
      actorselection.resolveOne().foreach(ref => {
        val future: Future[GuardianInfo] = (ref ? RequestGuardianInformations).mapTo[GuardianInfo]
        future.map(info => (member.address, info)).pipeTo(self)
      })

    case (address: Address, info@GuardianInfo(patch, state, _)) =>
      log.info(s"Received guardian info from $address: patch $patch, state: $state ")
      guardians = guardians + (address -> info)

    case MemberRemoved(member, previousStatus) if member.hasRole("guardian") =>
      log.info(s"member ${member.address} removed")
      if (guardians.contains(member.address)) {
        guardians = guardians - member.address
      }


    case info@GuardianInfo(patch, state, _) =>
      log.info(s"Guardian ${sender.path.address}: state $state")
      guardians = guardians + (sender.path.address -> info)
    // TODO: check if patch in alert and send message
  }


  private def isPatchInAlert(patch: Patch): Boolean = {
    val alertedGuardians = guardians.values.count { g => g.patch == patch && g.state == GuardianState.PREALERT }
    val totalGuardians = guardians.values.count { g => g.patch == patch }
    alertedGuardians / totalGuardians > 0.5
  }


}
