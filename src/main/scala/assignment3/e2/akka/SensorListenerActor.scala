package assignment3.e2.akka

import akka.actor.{Actor, ActorLogging, Terminated}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberJoined, MemberRemoved, MemberUp, UnreachableMember}
import akka.util.Timeout

import scala.concurrent.duration._

class SensorListenerActor extends Actor with ActorLogging {

  import context.dispatcher

  implicit val timeout = Timeout(3 seconds)

  val cluster = Cluster(context.system)

  var availableSensors: Map[String, Set[String]] = Map()

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberUp(member) if member.hasRole("sensor") =>

    case MemberRemoved(member, previousStatus) if member.hasRole("sensor") =>
      log.info("Member is Removed: {} after {}", member.address, previousStatus)

    case Terminated(ref) =>
    // TODO
  }

}
