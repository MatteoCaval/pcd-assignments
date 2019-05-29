package assignment3.e0;

public abstract class AbstractBasicAgent extends Thread {

	protected World world;
	protected Flag stopFlag;

	public AbstractBasicAgent(String name, World world, Flag stopFlag) {
		super(name);
		this.world = world;
		this.stopFlag = stopFlag;
	}
	
	protected void logd(String msg) {
		synchronized(System.out) {
			// System.out.println("["+getName()+"] " + msg);
		}
	}
	
	protected void log(String msg) {
		synchronized(System.out) {
			System.out.println("["+getName()+"] " + msg);
		}
	}
}
