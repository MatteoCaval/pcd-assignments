package assignment3.e2.rmi.stub_scheleton

case class SensorStr(id: String, sensor: Sensor) extends Serializable {
  def getSensor:Sensor = sensor

  def getId: String = id

}
