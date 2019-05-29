package assignment3.e0;

public class Particle {

	private P2d pos;
	private V2d vel;
	private double alpha;
	private double kAttr;
	private double mass;
	
	public Particle(P2d pos, V2d vel, double alpha, double mass, double kAttr) {
		this.pos = pos;
		this.vel = vel;
		this.alpha = alpha;
		this.kAttr = kAttr;
		this.mass = mass;
	}
	
	public void update(V2d force, double dt) {
		V2d acc = force.mul(1.0/mass);
		vel = vel.sum(acc.mul(dt));
		pos = pos.sum(vel.mul(dt));
	}
	
	public P2d getPos() {
		return pos;
	}
	
	public V2d getVel() {
		return vel;
	}
	
	public double getMass(){
		return mass;	
	}
	
	public double getAlpha() {
		return alpha;
	}
	
	public double getAttrCoeff() {
		return kAttr;
	}
}
