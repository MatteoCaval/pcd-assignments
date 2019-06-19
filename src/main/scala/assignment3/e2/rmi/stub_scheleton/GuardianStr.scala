package assignment3.e2.rmi.stub_scheleton

case class GuardianStr(id: String, patchId: Int, guardian: Guardian) extends Serializable {
  def getGuardian: Guardian = guardian

  def getPatchId: Int = patchId

  def getId: String = id

}
