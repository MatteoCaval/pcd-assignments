package assignment3.e2.common

import scala.util.Random

object PatchManager {
  val N: Int = Config.N
  val M: Int = Config.M

  val width: Double = 1000
  val height: Double = 500

  val patchNumber: Int = N * M

  val getPatches: List[Patch] = {

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

  def getRandomPositionInsideMap: P2d = {
    val x = Random.nextInt(width.toInt)
    val y = Random.nextInt(height.toInt)
    P2d(x, y)
  }
}

