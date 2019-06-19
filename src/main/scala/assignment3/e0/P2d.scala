package assignment3.e0

class P2d(var x: Double, var y: Double) extends Serializable {
  def sum(v: V2d) = new P2d(x + v.x, y + v.y)

  def sub(v: P2d) = new V2d(x - v.x, y - v.y)

  override def toString: String = "P2d(" + x + "," + y + ")"
}