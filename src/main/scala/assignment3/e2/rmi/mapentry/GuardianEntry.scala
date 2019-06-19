package assignment3.e2.rmi.mapentry

import assignment3.e2.rmi.remoteobjects.Guardian

case class GuardianEntry(private val id: String, private val patchId: Int, private val guardian: Guardian) extends Serializable {
  def getRemoteObject: Guardian = guardian

  def getPatchId: Int = patchId

  def getId: String = id

}
