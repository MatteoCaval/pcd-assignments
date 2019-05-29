package assignment3.e0;

/**
 * PCD 2018-2019 - Assignment #01
 * 
 * Particle system version with GUI
 * 
 * @author aricci
 *
 */
public class ParticleSystemWithGUI {
	public static void main(String[] args) {

		double dt = 0.01;		
		int windowSizeX = 1200;
		int windowSizeY = 1000;
		double scaleFactor = 10;
		boolean displayAllSnapshot = false;
		
		World world = new World(dt, displayAllSnapshot);		
		WorldViewer viewer = new WorldViewer(world, windowSizeX, windowSizeY, scaleFactor);
		Controller controller = new Controller(world, viewer);
		viewer.setController(controller);		
		viewer.show();
	}
}
