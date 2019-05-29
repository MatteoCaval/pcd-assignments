package assignment3.e0;

import java.util.LinkedList;
import java.util.Optional;

public class ParticleBuffer {

	private LinkedList<Particle> particles;
	
	public ParticleBuffer() {
		particles = new LinkedList<Particle>();
	}
	
	synchronized Optional<Particle> getNewParticleAvail() {
		if (particles.size() > 0) {
			return Optional.of(particles.remove());
		} else {
			return Optional.empty();
		}
	}
	
	synchronized void notifyNewParticle(Particle p) {
		particles.add(p);
	}
}
