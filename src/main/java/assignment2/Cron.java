package assignment2;

public class Cron {

	private boolean running;
	private long startTime;

	public Cron(){
		running = false;
	}
	
	public void start(){
		running = true;
		startTime = System.currentTimeMillis();
	}
	
	public Cron stop(){
		startTime = getTime();
		running = false;
		return this;
	}
	
	public long getTime(){
		if (running){
			return 	System.currentTimeMillis() - startTime;
		} else {
			return startTime;
		}
	}
}
