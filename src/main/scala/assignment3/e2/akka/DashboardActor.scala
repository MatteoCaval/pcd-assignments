package assignment3.e2.akka

import akka.actor.{Actor, ActorLogging, ActorRef, RootActorPath, Terminated}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberUp}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import assignment3.e2.akka.ActorMessages._
import assignment3.e2.common._

class DashboardActor extends Actor with ActorLogging {

  private var guardians: Map[ActorRef, GuardianInfo] = Map()
  private var sensors: Map[ActorRef, SensorData] = Map()

  private val cluster: Cluster = Cluster(context.system)
  private val mediator: ActorRef = DistributedPubSub(context.system).mediator

  mediator ! Subscribe(PubSubMessages.GUARDIAN_INFO, self)
  mediator ! Subscribe(PubSubMessages.SENSOR_DATA, self)


  val view = new MapMonitorViewImpl(new ViewListener {
    override def resetAlarmPressed(patchId: Int): Unit = {
      mediator ! Publish(PubSubMessages.TERMINATE_ALERT, PatchReleaseMessage(patchId))
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

    case Terminated(actorRef) =>
      if (guardians.contains(actorRef)) {
        log.info(s"Actor guardian ${guardians(actorRef).guardianId} terminated")
        this.view.notifyGuardianRemoved(guardians(actorRef).guardianId, guardians(actorRef).patch.id)
        guardians -= actorRef // controllare se crasha quando non presente

      }

      if (sensors.contains(actorRef)) {
        log.info(s"Actor sensor ${sensors(actorRef).sensorId} terminated")
        this.view.notifySensorRemoved(sensors(actorRef).sensorId)
        sensors -= actorRef
      }

  }


  private def uiInformationRetrieval: Receive = {
    case s@SensorData(id, _, position) =>
      log.info(s"Received position of sensor ${sender.path}: $s")
      if (!sensors.contains(sender)) {
        context.watch(sender)
      }
      sensors += (sender -> s)

      view.notifySensor(DashboardSensorPosition(id, position))


    case info@GuardianInfo(id, patch, state, temp) =>
      log.info(s"Received temperature from ${sender.path}: temperature ${info.temperature} at patch ${info.patch} with state $state")
      if (!guardians.contains(sender)) {
        context.watch(sender)
      }
      guardians += (sender -> info)


      val guardianState = state match {
        case GuardianState.OK => GuardianStateEnum.IDLE
        case GuardianState.ALERT => GuardianStateEnum.ALARM
        case GuardianState.PREALERT => GuardianStateEnum.PRE_ALERT
      }


      this.view.notifyAlarmStateEnabled(patch.id, state == GuardianState.ALERT)

      view.notifyGuardian(DashboardGuardianState(id, temp, guardianState, patch))

  }

}
