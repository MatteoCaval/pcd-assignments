package assignment3.e2.rmi.remoteobjects

import java.io.Serializable
import java.rmi.RemoteException

import assignment3.e2.common._

@SerialVersionUID(5377073057466013968L)
class SensorImpl(var name: String) extends Sensor with Serializable {
  var temperature: Option[Double] = None
  var position: P2d = SensorManager.getRandomPosition

  var lastUpdate:Long = 0

  @throws[RemoteException]
  override def getName: String = name

  @throws[RemoteException]
  override def tell(text: String): Unit = System.out.println(text)

  @throws[RemoteException]
  override def getDetection: SensorDetection = {
    fresh()
    SensorDetection(position, temperature)
  }

  @throws[RemoteException]
  override def getDashboardPosition: DashboardSensorPosition = {
    fresh()
    DashboardSensorPosition(name, position)
  }

  // allow to make live the sensor
  private def fresh(): Unit = {
    if (System.currentTimeMillis() - lastUpdate > Config.SENSOR_REFRESH_RATE){
      lastUpdate = System.currentTimeMillis()
      position = SensorManager.moveRandomly(position)
      temperature = SensorManager.getRandomTemperature
    }
  }
}