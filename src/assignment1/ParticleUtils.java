package assignment1;

import assignment1.common.V2d;

public class ParticleUtils {


    /*
    calculate force of j on i
     */
    public static V2d getForceBetweenParticle(Particle i, Particle j, double  k) {
        double dist = i.getPosition().getDistance(j.getPosition());
        double d3 = dist * dist * dist;
        double fx = i.getForce().x + k * i.getAlpha() * j.getAlpha() / d3 * (i.getPosition().x - j.getPosition().x);
        double fy = i.getForce().y + k * i.getAlpha() * j.getAlpha() / d3 * (i.getPosition().y - j.getPosition().y);

        return new V2d(fx, fy);
    }

}
