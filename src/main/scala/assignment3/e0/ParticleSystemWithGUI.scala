package assignment3.e0

object ParticleSystemWithGUI extends App {

  override def main(args: Array[String]): Unit = {
    val dt = 0.01
    val windowSizeX = 1200
    val windowSizeY = 1000
    val scaleFactor = 10

    val world = new World(dt)
    val viewer = new WorldViewer(world, windowSizeX, windowSizeY, scaleFactor)
    val controller = new Controller(world, viewer)
    viewer.setController(controller)
    viewer.show()
  }

}