package assignment3.e2.akka

import akka.actor.{Actor, ActorLogging, ActorRef, RootActorPath, Terminated}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberRemoved, MemberUp, UnreachableMember}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import akka.util.Timeout
import assignment3.e2.common.{DashboardGuardianState, DashboardSensorPosition, GuardianStateEnum, MapMonitorViewImpl, P2d, PatchManager, ViewListener}

import scala.concurrent.duration._

class DashboardActor extends Actor with ActorLogging {

  import context.dispatcher

  private var guardians: Map[ActorRef, GuardianInfo] = Map()
  private var sensors: Map[ActorRef, SensorData] = Map()

  private val cluster: Cluster = Cluster(context.system)
  private val mediator: ActorRef = DistributedPubSub(context.system).mediator

  mediator ! Subscribe(SubSubMessages.GUARDIAN_INFO, self)
  mediator ! Subscribe(SubSubMessages.SENSOR_DATA, self)
  mediator ! Subscribe(SubSubMessages.PATCH_ALERT, self)


  val view = new MapMonitorViewImpl(new ViewListener {
    override def resetAlarmPressed(patchId: Int): Unit = {
      println(patchId)
    }
  }, PatchManager.getPatches.size)

  override def preStart(): Unit = {
    view.show()
    cluster.subscribe(
      self,
      initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent]
    )
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = joinAndLeaveMessage.orElse(uiInformationRetrieval)

  private def joinAndLeaveMessage: Receive = {

    case MemberUp(member) if member.hasRole("sensor") =>
      log.info(s"Node ${member.address} up")
      context.actorSelection(RootActorPath(member.address) / "user" / "*") !
        RegistrateSensor


    case MemberUp(member) if member.hasRole("guardian") =>
      log.info(s"Node ${member.address} up")
      context.actorSelection(RootActorPath(member.address) / "user" / "*") !
        RequestGuardianInformations

    //    case MemberRemoved(member, previousStatus) if member.hasRole("guardian") =>
    //      log.info(s"Guardian ${member.address} removed")
    //
    //    case MemberRemoved(member, previousStatus) if member.hasRole("sensor") =>
    //      log.info(s"Sensor ${member.address} removed")

    case Terminated(actorRef) =>
      log.info(s"Actor ${actorRef.path} terminated")
      guardians -= actorRef // controllare se crasha quando non presente
      sensors -= actorRef
    //TODO update ui
  }


  private def uiInformationRetrieval: Receive = {
    case s@SensorData(id, _, position) =>
      log.info(s"Received position of sensor ${sender.path}: $s")
      if (!sensors.contains(sender)) {
        context.watch(sender)
      }
      sensors += (sender -> s)

      view.notifySensor(DashboardSensorPosition(id, position))
    // TODO: Update ui

    case info@GuardianInfo(id, patch, state, temp) =>
      log.info(s"Received temperature from ${sender.path}: temperature ${info.temperature} at patch ${info.patch}")
      if (!guardians.contains(sender)) {
        context.watch(sender)
      }
      guardians += (sender -> info)
      // TODO update ui
      view.notifyGuardian(DashboardGuardianState(id, temp, GuardianStateEnum.IDLE, patch))


    case PathInAlert(patch) => // FIXME
      log.info(s"Patch $patch in alert")
//      context.system.scheduler.scheduleOnce(4 seconds) { // TODO: UI event
//        mediator ! Publish(SubSubMessages.TERMINATE_ALERT, patch)
//      }
  }

}
