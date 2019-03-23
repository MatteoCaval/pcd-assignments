package assignment1;

import assignment1.common.V2d;

public class ParticleUtils {

    public static V2d getForceBetweenParticle(Particle particle1, Particle particle2, int k) {
        double dist = particle1.getPosition().getDistance( particle2.getPosition() );
        double d3 = dist * dist * dist;
        double fx = k * particle1.getAlpha() * particle2.getAlpha() / d3 * (particle1.getPosition().x - particle2.getPosition().x);
        double fy = k * particle1.getAlpha() * particle2.getAlpha() / d3 * (particle1.getPosition().y - particle2.getPosition().y);

        return new V2d( fx, fy );
    }

}
