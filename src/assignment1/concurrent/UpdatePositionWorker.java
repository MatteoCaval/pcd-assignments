package assignment1.concurrent;

import assignment1.Particle;

import java.util.List;

public class UpdatePositionWorker extends ParticleWorker {

    private int timeElapsed = 2; //TODO: sistemare better

    public UpdatePositionWorker(int from, int to, List<Particle> particles) {
        super(from, to, particles);
    }

    @Override
    public void run() {

        System.out.println("PW from " + from + " to " + to);

        particles.subList(from, to).forEach(particle -> {
            particle.getPosition().x += timeElapsed * particle.getSpeed().x;
            particle.getPosition().y += timeElapsed * particle.getSpeed().y;
            particle.getSpeed().x += timeElapsed * particle.getForce().x / particle.getmConst();
            particle.getSpeed().y += timeElapsed * particle.getForce().y / particle.getmConst();
        });


    }
}
