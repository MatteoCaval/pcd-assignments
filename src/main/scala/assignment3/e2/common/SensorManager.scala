package assignment3.e2.common

import assignment3.e2.rmi.Config

import scala.util.Random

object TestRandoms extends App {
  println(SensorManager.getRandomPosition.toString)
  println(SensorManager.getRandomPosition.toString)
  println(SensorManager.getRandomPosition.toString)
  println(SensorManager.getRandomPosition.toString)

  var x = SensorManager.getRandomPosition
  println(SensorManager.moveRandomly(x).toString)
  println(SensorManager.moveRandomly(x).toString)
  println(SensorManager.moveRandomly(x).toString)
  println(SensorManager.moveRandomly(x).toString)

  println(SensorManager.getRandomTemperature)
  println(SensorManager.getRandomTemperature)
  println(SensorManager.getRandomTemperature)
  println(SensorManager.getRandomTemperature)
}


object SensorManager {

  def getRandomPosition: P2d ={
    val x = getRandomBetweenNumbers(0, CommonConfig.MAX_MAP_X)
    val y = getRandomBetweenNumbers(0, CommonConfig.MAX_MAP_Y)

    P2d(x,y)
  }

  def moveRandomly(currentPos: P2d): P2d = {
    val vector_x = getRandomBetweenNumbers(-Config.MAX_MOVE_RANGE, Config.MAX_MOVE_RANGE)
    val vector_y = getRandomBetweenNumbers(-Config.MAX_MOVE_RANGE, Config.MAX_MOVE_RANGE)

    val newX = currentPos.x + vector_x
    val newY = currentPos.y + vector_y

    P2d(newX, newY)
  }

  def getRandomTemperature: Option[Double] = {
    Some(getRandomBetweenNumbers(CommonConfig.MIN_TEMP, CommonConfig.MAX_TEMP))
  }

  private def getRandomBetweenNumbers(start: Double, end: Double): Double ={
    val r = new Random()
    start + (end - start) * r.nextDouble
  }
}
