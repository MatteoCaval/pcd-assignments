package assignment3.e0;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class World {

	private ArrayList<Particle> particles;
	private ArrayList<P2d> currentPosSnapshot;

	private BlockingQueue<WorldSnapshot> snapBuffer;
	private WorldSnapshot snapToDisplay;
	private boolean displayAllSnapPolicy;	
	private double kParam;
	
	private double dt;
	private long currentStep;


	public World(double dt){
		this(dt, true);
	}
	
	public World(double dt, boolean displayAllSnapPolicy){
		this.dt = dt;
		this.displayAllSnapPolicy = displayAllSnapPolicy;
		kParam = 1;
		particles = new ArrayList<Particle>();
		currentPosSnapshot = new ArrayList<P2d>();
		snapBuffer = new LinkedBlockingQueue<WorldSnapshot>();
	}

	public void init(int nParticles) {
		particles.clear();
		Random rand = new Random(System.currentTimeMillis());
		for (int i = 0; i < nParticles; i++) {
			double x = rand.nextDouble()*2 - 1;
			double y = rand.nextDouble()*2 - 1;
			particles.add(new Particle(new P2d(x, y), new V2d(0, 0), 1, 1, 1));
		}
		currentStep = 0;
	}
	
	public Particle getBody(int i) {
		return particles.get(i);
	}
		
	public void backupPositions() {
		currentPosSnapshot.clear();
		for (Particle p: particles) {
			currentPosSnapshot.add(p.getPos());
		}
	}

	public void pushSnapshotToDisplay() {
		try {
			if (displayAllSnapPolicy) {
				snapBuffer.put(new WorldSnapshot(this.currentPosSnapshot, this.getCurrentTime()));
			} else {
				snapToDisplay = new WorldSnapshot(this.currentPosSnapshot, this.getCurrentTime());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void addParticle(Particle p) {
		particles.add(p);
	}
	
	public WorldSnapshot getSnapshotToDisplay() {
		if (displayAllSnapPolicy) {
			return snapBuffer.poll();
		} else {
			return snapToDisplay;
		}
	}
	
	public int getNumParticles() {
		return particles.size();
	}
	
	public ArrayList<P2d>  getCurrentPosSnaphot() {
		return currentPosSnapshot;
	}
	
	public V2d computeForces(int indexBody) {		
		V2d force = new V2d(0,0);
		Particle b = particles.get(indexBody);
		P2d pos = currentPosSnapshot.get(indexBody);
		double alpha = b.getAlpha();		
		
		for (int i = 0; i < particles.size(); i++) {
			if (i != indexBody) {
				Particle b2 =  particles.get(i);
				P2d pos2 = currentPosSnapshot.get(i);
				V2d ds = pos.sub(pos2);
				double dist = ds.abs();
				double invd = 1.0 /  (dist*dist*dist);
				V2d f = ds.mul(kParam).mul(alpha).mul(b2.getAlpha()).mul(invd);
				force = force.sum(f);
			}
		}

		force = force.sum((b.getVel().mul(-b.getAttrCoeff())));
		return force;
	}
	
	public void updateTime() {
		currentStep++;
	}
		
	public double getTimestep() {
		return dt;
	}
	
	public long getCurrentStep() {
		return currentStep;
	}
	
	public double getCurrentTime() {
		return currentStep*dt;
	}
	
	public void dump() {
		synchronized(System.out) {
			System.out.println(" == WORLD == ");
			for (Particle p: particles) {
				System.out.println("Particle pos: " + p.getPos() + " vel: " + p.getVel());
			}
			System.out.println(" ========== ");
		}
	}
}
