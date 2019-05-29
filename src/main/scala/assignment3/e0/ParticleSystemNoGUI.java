package assignment3.e0;

/**
 * PCD 2018-2019 - Assignment #01
 * 
 * Particle system version for profiling purposes (no GUI)
 * 
 * @author aricci
 *
 */
public class ParticleSystemNoGUI {
	public static void main(String[] args) {
		
		int nParticles = 1000;
		int nSteps = 1000;
		double dt = 0.01;
		
		World world = new World(dt);
		Flag stopFlag = new Flag();
		ParticleMaster master = new ParticleMaster(world, stopFlag, nParticles, nSteps);
		master.start();
	}

}
