package assignment1.concurrent;

import assignment1.Particle;

import java.util.List;

abstract class ParticleWorker extends Thread {

    protected final List<Particle> particles;
    protected final int from, nparticles;

    public ParticleWorker(int from, int nparticles, List<Particle> particles) {
        this.from = from;
        this.nparticles = nparticles;
        this.particles = particles;
    }


}
