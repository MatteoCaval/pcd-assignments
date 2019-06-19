package assignment3.e2.rmi.mapentry

import assignment3.e2.rmi.remoteobjects.Guardian

case class GuardianEntry(id: String, patchId: Int, guardian: Guardian) extends Serializable {
  def getRemoteObject: Guardian = guardian

  def getPatchId: Int = patchId

  def getId: String = id

}
