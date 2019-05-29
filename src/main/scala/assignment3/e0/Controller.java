package assignment3.e0;

public class Controller {

	private World world;
	private WorldViewer viewer;
	private ParticleMaster master;
	private Flag stopFlag;
	private ParticleBuffer newParticles;
	
	public Controller(World world, WorldViewer viewer) {
		this.world = world;
		this.viewer = viewer;
		this.newParticles = new ParticleBuffer();
	}
	
	public void notifyStarted(int nParticles) {
		stopFlag = new Flag();
		master = new ParticleMaster(world, stopFlag, nParticles, viewer, newParticles);
		master.start();
	}
	
	public void notifyStopped() {
		stopFlag.set();
	}
	
    public void notifyNewParticle(P2d pos) {
    		/* adding a particle with 100 times the mass and 10 times the charge */
    		newParticles.notifyNewParticle(new Particle(pos, new V2d(0,10), 1000, 10, 1));
    }
	
}
