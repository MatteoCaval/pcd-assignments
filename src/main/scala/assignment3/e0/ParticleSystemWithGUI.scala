package assignment3.e0

/**
  * PCD 2018-2019 - Assignment #01
  *
  * Particle system version with GUI
  *
  * @author aricci
  *
  */
object ParticleSystemWithGUI extends App {
  override def main(args: Array[String]): Unit = {
    val dt = 0.01
    val windowSizeX = 1200
    val windowSizeY = 1000
    val scaleFactor = 10
    val displayAllSnapshot = false

    val world = new World(dt, displayAllSnapshot)
    val viewer = new WorldViewer(world, windowSizeX, windowSizeY, scaleFactor)
    val controller = new Controller(world, viewer)
    viewer.setController(controller)
    viewer.show()
  }
}