package pcd.assignment1;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Context {

    private final Boundary boundary;
    private final ArrayList<Particle> particles;

    public Context() {
        this.boundary = new Boundary(0, 0, 10, 10);
        this.particles = new ArrayList<>();
    }

    public void createParticle(double x, double y) {
        Particle particle = new Particle(x, y);
        this.particles.add(particle);
    }

    public void printAllParticles() {
        particles.stream().forEach(p -> System.out.println(p));
    }
}
