package assignment3.e2.akka

import akka.actor.{Actor, ActorLogging, ActorRef, Address}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberUp, UnreachableMember}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import akka.util.Timeout
import assignment3.e2.common.Patch

import scala.concurrent.duration._
import akka.pattern.ask
import akka.pattern.pipe

import scala.concurrent.Future

case object RequestGuardianInformations


class GuardianListenerActor extends Actor with ActorLogging {



  import context.dispatcher

  implicit val timeout = Timeout(3 seconds)

  val cluster = Cluster(context.system)

  var guardians: Map[Address, GuardianInfo] = Map()

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

    case (address: Address, info@GuardianInfo(patch, state)) =>
      log.info(s"Received guardian info from $address: patch $patch, state: $state ")
      guardians = guardians + (address -> info)


  }

}
