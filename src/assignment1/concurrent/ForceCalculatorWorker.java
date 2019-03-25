package assignment1.concurrent;

import assignment1.Particle;
import assignment1.ParticleUtils;
import assignment1.common.V2d;

import java.util.List;

public class ForceCalculatorWorker extends ParticleWorker {

    private double k;

    public ForceCalculatorWorker(int from, int nparticles, List<Particle> particles, double k) {
        super(from, nparticles, particles);
        this.k = k;
    }


    @Override
    public void run() {
        int to = from + nparticles;

        for (int i = from; i < to; i++) {

            V2d particleForce = particles.get(i).getForce();

            for (int j = 0; j < particles.size(); j++) {
                if (i != j) {
                    particleForce = particleForce.sum(ParticleUtils.getForceBetweenParticle(particles.get(i), particles.get(j), k));

                }

            }

            particles.get(i).setForce(particleForce);

        }


    }

}
