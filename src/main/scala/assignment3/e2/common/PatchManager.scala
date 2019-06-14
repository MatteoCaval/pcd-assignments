package assignment3.e2.common

import assignment3.e2.akka.Config


object PatchManager {
  val N: Int = Config.N
  val M: Int = Config.M

  val width: Double = 1000
  val height: Double = 500

  def getPatches: List[Patch] = {

    var list: List[Patch] = List()
    val deltaW = width / N
    val deltaH = height / M

    for (i <- 0 until M) {
      for (j <- 0 until N) {
        // id della Patch ottenuto con list.size -> 0,1,2,3..
        list = list :+ Patch(list.size, P2d(j * deltaW, i * deltaH), P2d(j * deltaW + deltaW, i * deltaH + deltaH))
      }
    }
    list
  }
}

object Test extends App {
  println(PatchManager.getPatches.toString)
}
