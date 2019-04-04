package assignment1;

import assignment1.common.P2d;
import assignment1.common.V2d;

public class Particle {

    private static final double ALPHA_CONST = 5;
    private static final double M_CONST = 1;
    private static final double K_FRICTION = 0.5;

    private P2d position;
    private V2d speed = new V2d(0, 0);
    private V2d force = new V2d(0, 0);

    public Particle(double x, double y) {
        this.position = new P2d(x, y);
    }

    public P2d getPosition() {
        return this.position;
    }

    public double getAlpha() {
        return ALPHA_CONST;
    }

    public double getFriction() {
        return K_FRICTION;
    }


    @Override
    public String toString() {
        return "Particle(" + position.x + "," + position.y + ")" + " Force: (" + force.x + "," + force.y + ")";
    }

    public void setForce(V2d particleForce) {
        this.force = particleForce;
    }

    public V2d getForce() {
        return this.force;
    }

    public V2d getSpeed() {
        return speed;
    }

    public double getmConst() {
        return M_CONST;
    }
}
