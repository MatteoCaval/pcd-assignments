package assignment3.e2.akka

import akka.actor.{Actor, ActorLogging, ActorRef, RootActorPath, Terminated}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberRemoved, MemberUp, UnreachableMember}

class TestActor extends Actor with ActorLogging {


  private val cluster = Cluster(context.system)
  private var sensors: Map[ActorRef, String] = Map()


  override def preStart(): Unit = cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberUp(member) if member.hasRole("sensor") =>
      context.actorSelection(RootActorPath(member.address) / "user" / "*") !
        RegistrateSensor

    case SensorRegistrationData(sensorId, ref) =>
      sensors = sensors + (ref -> sensorId)
      context.watch(ref)
      log.info(s"Sensor $sensorId with ${ref.path} registered, new size: ${sensors.size}")

    case MemberRemoved(member, previousStatus) if member.hasRole("sensor") =>
      sensors = sensors.filterNot(s => s._1.path.address.toString.contains(member.address.toString))
      log.info(s"Node ${member.address} removed, new size: ${sensors.size}")

    case Terminated(ref) =>
      sensors -= ref
      log.info(s"Actor $ref terminated, new size: ${sensors.size}")

  }

}
