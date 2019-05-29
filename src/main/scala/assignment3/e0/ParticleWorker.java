package assignment3.e0;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class ParticleWorker extends AbstractBasicAgent {

	/* particles in charge */
	private int from;
	private int to;

	/* for coordination with the master */
	private Semaphore nextStep;
	private ResettableLatch stepDone;

	/* for managing new particles */
	private volatile boolean newParticle;
	
	/* for identifier purposed */
	static private AtomicInteger workerId = new AtomicInteger(0);
	
	public ParticleWorker(World world, Flag stopFlag, Semaphore nextStep, ResettableLatch stepDone, int from, int num) {
		super("worker-" + workerId.incrementAndGet(), world, stopFlag);
		this.nextStep = nextStep;
		this.stepDone = stepDone;
		this.from = from; 
		this.to = from + num - 1;
	}
	
	public void run() {
		log("Working from " + from + " to " + to);
		double dt = world.getTimestep();
		while (!stopFlag.isSet()) {
			try {
				logd("waiting next step.");
				
				/* wait for master signal */
				nextStep.acquire();
				
				/* extension | managing dynamically added bodies */
				checkForNewParticles();
				
				/* update bodies in charge */
				for (int i = from; i <= to; i++) {
					Particle body = world.getBody(i);
					V2d force = world.computeForces(i);
					body.update(force, dt);
				}	
				logd("job done.");
				
				/* notify completion */
				stepDone.down();
				
			} catch (Exception ex) {
				// ex.printStackTrace();
			}
		}
		log("completed.");
	}
	
	/*
	 * it works only for the last worker 
	 */
	private void checkForNewParticles() {
		if (newParticle) {
			to++;
			newParticle = false;
		}
	}
	
	public void notifyNewParticle() {
		newParticle = true;
	}
}
