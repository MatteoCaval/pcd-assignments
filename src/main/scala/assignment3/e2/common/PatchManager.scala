package assignment3.e2.common


object PatchManager {

  val N = 4 //width
  val M = 2

  val width: Double = 100
  val height: Double = 50

  def getPatches(): List[Patch] = {

    var list: List[Patch] = List()
    val deltaW = width / N
    val deltaH = height / M

    for (i <- 0 until M) {
      for (j <- 0 until N) {
        list = list :+ Patch(P2d(j * deltaW, i * deltaH), P2d(j * deltaW + deltaW, i * deltaH + deltaH))
      }
    }
    list
  }

}

object Test extends App {
  println(PatchManager.getPatches().toString)
}
