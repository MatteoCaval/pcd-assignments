/*
 * Created on Feb 10, 2005
 *
 */
package assignment1;

/**
 * @author aricci
 */
public class Boundary {
    private double x0;
    private double y0;
    private double x1;
    private double y1;

    public Boundary(double x0, double y0, double x1, double y1) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
    }

    public double getX0() {
        return x0;
    }

    public double getX1() {
        return x1;
    }

    public double getY0() {
        return y0;
    }

    public double getY1() {
        return y1;
    }

    public double getHeight(){
        return Math.abs(this.x0) + this.x1;
    }

    public double getWidth(){
        return Math.abs(this.y0) + this.y1;
    }
}
