package assignment1.concurrent;

import assignment1.Particle;

import java.util.List;

abstract class ParticleWorker extends Thread {

    protected final List<Particle> particles;
    protected final int from, to;

    public ParticleWorker(int from, int to, List<Particle> particles) {
        this.from = from;
        this.to = to;
        this.particles = particles;
    }


}
