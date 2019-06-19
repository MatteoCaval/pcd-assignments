package assignment3.e1

import scala.collection.mutable.ArrayBuffer

class WorldSnapshot(val pos: ArrayBuffer[P2d], var time: Double) {
  var posList: ArrayBuffer[P2d] = new ArrayBuffer(pos.length)
  var index = 0

  for (p <- pos) {
    posList += p
  }

  def getPosList: ArrayBuffer[P2d] = posList

  def getTime: Double = time
}