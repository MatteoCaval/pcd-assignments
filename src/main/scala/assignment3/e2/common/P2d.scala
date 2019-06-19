package assignment3.e2.common

case class P2d(x: Double, y: Double) {

  override def equals(obj: Any): Boolean = {
    obj match {
      case p2d: P2d => if (p2d.x == x && p2d.y == y) return true
      case _ => false
    }
    false
  }
}
