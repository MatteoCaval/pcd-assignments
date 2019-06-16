//package assignment3.e2.akka
//
//import akka.actor.{Actor, ActorLogging, ActorRef, Address}
//import akka.cluster.Cluster
//import akka.cluster.ClusterEvent._
//import akka.cluster.pubsub.DistributedPubSub
//import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
//import akka.pattern.{ask, pipe}
//import akka.util.Timeout
//import assignment3.e2.common.Patch
//
//import scala.concurrent.Future
//import scala.concurrent.duration._
//
//case object RequestGuardianInformations
//
//case class GuardianRemoved(address: String)
//
//case class PathInAlert(patch: Patch)
//
//class GuardianListenerActor extends Actor with ActorLogging {
//
//  import context.dispatcher
//
//  implicit val timeout: Timeout = Timeout(5 seconds)
//  val cluster: Cluster = Cluster(context.system)
//
//
//  var guardians: Map[Address, GuardianInfo] = Map()
//
//  val mediator: ActorRef = DistributedPubSub(context.system).mediator
//  mediator ! Subscribe(SubSubMessages.GUARDIAN_INFO, self)
//  mediator ! Subscribe(SubSubMessages.TERMINATE_ALERT, self)
//
//  override def preStart(): Unit = {
//    cluster.subscribe(
//      self,
//      initialStateMode = InitialStateAsEvents,
//      classOf[MemberEvent]
//    )
//  }
//
//  override def postStop(): Unit = cluster.unsubscribe(self)
//
//  override def receive: Receive = {
//
//    case MemberUp(member) if member.hasRole("guardian") =>
//      val actorselection = context.actorSelection(s"${member.address}/user/guardian")
//      log.info(s"member up: ${member.address}")
//      actorselection.resolveOne().foreach(ref => {
//        val future: Future[GuardianInfo] = (ref ? RequestGuardianInformations).mapTo[GuardianInfo]
//        future.map(info => (member.address, info)).pipeTo(self)
//      })
//
//
//
//    case (address: Address, info@GuardianInfo(_, patch, state, _)) =>
//      log.info(s"Received guardian info from $address: patch $patch, state: $state ")
//      guardians = guardians + (address -> info)
//
//    case MemberRemoved(member, previousStatus) if member.hasRole("guardian") =>
//      log.info(s"member ${member.address} removed")
//      if (guardians.contains(member.address)) {
//        guardians = guardians - member.address
//      }
//
//    case info@GuardianInfo(_, patch, state, _) =>
//      log.info(s"Guardian ${sender.path.address}: state $state")
//      guardians = guardians + (sender.path.address -> info)
//      // TODO: check if patch in alert and send message
//      if (isPatchInAlert(patch)) {
//        startAlertProcedure(patch)
//      }
//
//    case patch: Patch =>
//      log.info(s"alert for patch $patch terminated")
//      alertProcedures = alertProcedures - patch
//
//
//  }
//
//
//  private def isPatchInAlert(patch: Patch): Boolean = {
//    val alertedGuardians = guardians.values.count { g => g.patch == patch && g.state == GuardianState.PREALERT }
//    val totalGuardians = guardians.values.count { g => g.patch == patch }
//    alertedGuardians / totalGuardians > 0.5
//  }
//
//
//  private var alertProcedures: Map[Patch, Boolean] = Map()
//
//  private def startAlertProcedure(patch: Patch): Unit = {
//    if (!alertProcedures.contains(patch) || !alertProcedures(patch)) {
//      alertProcedures = alertProcedures + (patch -> true)
//      log.info(s"Alert procedure for patch $patch started")
//      context.system.scheduler.scheduleOnce(4 seconds) {
//        if (isPatchInAlert(patch)) {
//          mediator ! Publish(SubSubMessages.PATCH_ALERT, PathInAlert(patch))
//        } else {
//          alertProcedures = alertProcedures - patch
//        }
//      }
//    }
//
//
//  }
//
//
//}
