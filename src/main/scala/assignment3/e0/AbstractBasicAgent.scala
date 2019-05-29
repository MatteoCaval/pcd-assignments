package assignment3.e0

abstract class AbstractBasicAgent(val name: String, var world: World, var stopFlag: Flag) extends Thread(name) {
  protected def log(msg: String): Unit = System.out synchronized System.out.println("[" + getName + "] " + msg)
}