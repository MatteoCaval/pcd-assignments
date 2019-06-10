package assignment3.e2.akka

case class Patch(topPoint: P2d, bottomPoint: P2d) {

  def includePoint(point: P2d): Boolean =
    point.x >= topPoint.x &&
      point.y >= topPoint.y &&
      point.x < bottomPoint.x &&
      point.y < bottomPoint.y

}
