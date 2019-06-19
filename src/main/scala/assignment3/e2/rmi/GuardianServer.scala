package assignment3.e2.rmi

import java.rmi.registry.LocateRegistry
import java.rmi.server.UnicastRemoteObject
import java.util.UUID

import assignment3.e2.common.{GuardianStateEnum, PatchManager}
import assignment3.e2.rmi.stub_scheleton.{Guardian, GuardianImpl, GuardianStr, MapMonitor}

import scala.util.Random

object GuardianServer extends App {
  val TAG = "GuardianServer - "

  val host = null
  //if (args.length < 1) null else args(0)
  val id = if (args.length < 3) UUID.randomUUID().toString else args(0)
  val patchNum: Int = if (args.length < 3) Random.nextInt(PatchManager.getPatches.size) else args(1).toInt
  val port: Int = if (args.length < 3) Random.nextInt(255) else args(2).toInt

  var guardian: Guardian = _

  try {
    println(TAG + "Started")

    val registry = LocateRegistry.getRegistry("169.254.38.10", 1500)
    val server = registry.lookup("monitorObj").asInstanceOf[MapMonitor]

    val assignedPatch = PatchManager.getPatches(patchNum)
    guardian = new GuardianImpl(id, assignedPatch)
    UnicastRemoteObject.exportObject(guardian, port)

    server.addGuardian(GuardianStr(id, patchNum, guardian))
  } catch {
    case e: Exception =>
      println(TAG + "Exception: " + e)
      e.printStackTrace()
  }

  var state = GuardianStateEnum.IDLE

  while (true) {
    Thread.sleep(2000)

    state = guardian.getState

    if (state != GuardianStateEnum.ALARM){
      guardian.freshSensorDetections()
    } else {
      println("Client: sono in allarme, non posso fare nulla")
    }
  }
}