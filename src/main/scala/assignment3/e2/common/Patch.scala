package assignment3.e2.common

case class Patch(topPoint: P2d, bottomPoint: P2d) {

  def includePoint(point: P2d): Boolean =
    point.x >= topPoint.x &&
      point.y >= topPoint.y &&
      point.x < bottomPoint.x &&
      point.y < bottomPoint.y

  override def equals(obj: Any): Boolean = {
    obj match {
      case patch: Patch => if (patch.bottomPoint == bottomPoint && patch.topPoint == topPoint) return true
      case _ =>
    }
    false
  }


}
