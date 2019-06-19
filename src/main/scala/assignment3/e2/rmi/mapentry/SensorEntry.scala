package assignment3.e2.rmi.mapentry

import assignment3.e2.rmi.remoteobjects.Sensor

case class SensorEntry(id: String, sensor: Sensor) extends Serializable {
  def getRemoteObject:Sensor = sensor

  def getId: String = id

}
