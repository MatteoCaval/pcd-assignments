package assignment1;

import assignment1.common.P2d;
import assignment1.common.V2d;

public class Particle {

    private static final int ALPHA_CONST = 1;
    private static final int M_CONST = 1;

    private P2d position;
    private V2d speed;
    private V2d force = new V2d( 0,0 );

    public Particle(double x, double y) {
        this.position = new P2d( x, y );
    }

    public P2d getPosition() {
        return new P2d( position.x, position.y );
    }

    public int getAlpha() {
        return ALPHA_CONST;
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
}
