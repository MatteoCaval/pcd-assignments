package assignment3.e0;

import java.util.Optional;
import java.util.concurrent.Semaphore;

public class ParticleMaster extends AbstractBasicAgent {

	private Semaphore[] nextSteps;
	private ResettableLatch stepDone;
	private ParticleWorker[] workers; 
	private WorldViewer viewer;
	private int nWorkers;
	private ParticleBuffer newParticles;
	private int nSteps;
	private int nParticles;

	/*
	 * Master with GUI
	 */
	public ParticleMaster(World world, Flag stopFlag, int nParticles, WorldViewer viewer,  ParticleBuffer buffer) {
		super("master", world, stopFlag);
		this.viewer = viewer;
		this.nParticles = nParticles;
		this.newParticles = buffer;
	}

	/*
	 * Master without a GUI
	 */
	public ParticleMaster(World world, Flag stopFlag, int nParticles, int nSteps) {
		super("master", world, stopFlag);
		this.nSteps = nSteps;
		this.nParticles = nParticles;
	}
	
	public void run() {
		
		logd("init world...");
		world.init(nParticles);		

		logd("init workers...");
		initWorkers();
		
		logd("starting simulation.  ");
		if (viewer != null) {
			doSimulationWithGUI();
		} else {
			doSimulationWithChrono(nSteps);
		}
	}
	
	private void initWorkers() {
		nWorkers = Runtime.getRuntime().availableProcessors() + 1;
		logd("creating workers " + nWorkers);
		workers = new ParticleWorker[nWorkers];
		nextSteps = new Semaphore[nWorkers];				
		stepDone = new ResettableLatch(nWorkers);
		
		int nPartPerWorker = world.getNumParticles() / nWorkers;
		int nRem = world.getNumParticles() % nWorkers;
		int from = 0;
		for (int i = 0; i < nWorkers; i++) {
			nextSteps[i] = new Semaphore(0);
			int num = nPartPerWorker;
			if (nRem > 0) {
				num++;
				nRem--;
			}
			workers[i] = new ParticleWorker(world, stopFlag, nextSteps[i], stepDone, from, num);
			workers[i].start();
			from = from + num;
		}
	}
		
	private void doSimulationWithGUI() {
		world.backupPositions();		
		while (!stopFlag.isSet()) {			
			stepDone.reset();
			
			/* notify workers to make a new step */
			for (Semaphore s: nextSteps) {
				s.release();
			}			
			try {
				/* wait for all workers to complete their job */
				stepDone.await();
				
				/* check for new particles to add */
				Optional<Particle> newPart = newParticles.getNewParticleAvail();
				if (newPart.isPresent()) {
					world.addParticle(newPart.get());
					workers[workers.length - 1].notifyNewParticle();
				}
				
				/* update world */
				world.backupPositions();
				world.pushSnapshotToDisplay();
				world.updateTime();
				
				/* update view */
				viewer.updateView();

				// Thread.sleep(20);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}	
		log("completed.");
		for (ParticleWorker pw: workers) {
			pw.interrupt();
		}
	}
	
	private void doSimulationWithChrono(int nSteps) {
		world.backupPositions();
		
		Chrono chrono = new Chrono();
		chrono.start();
		log("Started.");
		
		while (world.getCurrentStep() < nSteps) {
			stepDone.reset();
			
			/* notify workers to make a new step */
			for (Semaphore s: nextSteps) {
				s.release();
			}
			try {
				/* wait for all workers to complete their job */				
				stepDone.await();
				
				/* update world */
				world.backupPositions();
				world.updateTime();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		chrono.stop();
		long dt = chrono.getTime();
		double timePerStep = ((double) dt)/nSteps;
		log("Done " + nSteps + " steps with " + world.getNumParticles() +" particles using " + nWorkers + " workers in: " + dt + "ms");
		log("- " + timePerStep + " ms per step");
		System.exit(0);
	}
}
