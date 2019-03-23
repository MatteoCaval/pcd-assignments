package pcd.assignment1;

import pcd.demo.common.P2d;
import pcd.demo.common.V2d;

import java.awt.geom.Point2D;

public class Particle {

    private static final int ALPHA = 1;

    private P2d position;
    private V2d velocity;

    public Particle(double x, double y) {
        this.position = new P2d(x, y);
    }

    public P2d getPos() {
        return new P2d(position.x, position.y);
    }

    @Override
    public String toString() {
        return "Particle(" + position.x + "," + position.y + ")";
    }
}
