package assignment1.concurrent;

import assignment1.Particle;

import java.util.List;

abstract class ParticleWorker extends Thread {

    protected final List<Particle> particles;
    protected final int from, to;
    protected final Object forceLock;
    protected final Object positionLock;
    protected Counter counter;


    public ParticleWorker(int from, int to, List<Particle> particles, Object forceLock, Object positionLock, Counter counter) {
        this.from = from;
        this.to = to;
        this.particles = particles;
        this.forceLock = forceLock;
        this.positionLock = positionLock;
        this.counter = counter;
    }


}
