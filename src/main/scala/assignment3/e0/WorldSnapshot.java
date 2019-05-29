package assignment3.e0;

import java.util.ArrayList;

/**
 * 
 *
 */
public class WorldSnapshot {
	
	private P2d[] posList;
	private double time;
	
	public WorldSnapshot(ArrayList<P2d> pos, double time) {
		posList = new P2d[pos.size()];
		int index = 0;
		for (P2d p: pos) {
			posList[index++] = p;
		}
		this.time = time;
	}
	
	public P2d[] getPosList() {
		return posList;
	}
	
	public double getTime() {
		return time;
	}
}
