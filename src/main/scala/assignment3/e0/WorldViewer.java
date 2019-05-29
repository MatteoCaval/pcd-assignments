package assignment3.e0;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;

import javax.swing.*;

import pcd.demo.common.*;
import pcd.lab04.mandel_ok_noconcur.InputListener;
import pcd.lab04.mandel_ok_noconcur.MandelbrotPanel;


public class WorldViewer {
    
    private VisualiserFrame frame;
    private World world;
    private double scale;
    private Controller controller;
    
    public WorldViewer(World world, int w, int h, double scale){
    		this.world = world;
    		this.scale = scale;
    		
    		frame = new VisualiserFrame(w, h);
        frame.setResizable(false);        
    }
 
    public void show() {
		frame.setVisible(true);
}

    public void updateView() {
    		frame.repaint();
    }
            
    public void setController(Controller c) {
    		controller = c;
    }
    
    protected void notifyNewParticle(P2d pos) {
    		controller.notifyNewParticle(pos);
    }
    
    class VisualiserFrame extends JFrame implements ActionListener {
	
	    	private JButton startButton;
	    	private JButton stopButton;
	    	private JButton zoomIn;
	    	private JButton zoomOut;	    	
	    	private JTextField time;
	    	private VisualiserPanel setPanel;
	    	private JTextField nParticles;
	    	
	    	public VisualiserFrame(int w, int h){
	    		super(".:: Particle System ::.");
	    		setSize(w,h);

	    		nParticles = new JTextField(5);
	    		nParticles.setText("2000");
	    			    		
	    		startButton = new JButton("start");
	    		stopButton = new JButton("stop");
	    		zoomIn = new JButton("zoom in");
	    		zoomOut = new JButton("zoom out");
	
	    		JPanel controlPanel = new JPanel();
	    		
	    		controlPanel.add(startButton);
	    		controlPanel.add(stopButton);
	    		controlPanel.add(zoomIn);
	    		controlPanel.add(zoomOut);
	    		controlPanel.add(new JLabel("Num Particles"));
	    		controlPanel.add(nParticles);
	    		
	    		setPanel = new VisualiserPanel(w,h); 
	
	    		JPanel infoPanel = new JPanel();
	    		time = new JTextField(20);
	    		time.setText("Idle");
	    		time.setEditable(false);
	    		infoPanel.add(new JLabel("Time"));
	    		infoPanel.add(time);
	    		
	    		JPanel cp = new JPanel();
	    		LayoutManager layout = new BorderLayout();
	    		cp.setLayout(layout);
	    		cp.add(BorderLayout.NORTH,controlPanel);
	    		cp.add(BorderLayout.CENTER,setPanel);
	    		cp.add(BorderLayout.SOUTH, infoPanel);
	    		setContentPane(cp);		

	    		setNotRunningConfig();
	    		
	    		startButton.addActionListener(this);
	    		stopButton.addActionListener(this);
	    		zoomIn.addActionListener((ActionEvent ev) -> {
	    			scale = scale*1.1;
	    		});
	    		zoomOut.addActionListener((ActionEvent ev) -> {
	    			scale = scale*0.9;
	    		});
	    		
	    		setDefaultCloseOperation(EXIT_ON_CLOSE);
	    	}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == startButton) {
				setRunningConfig();
				controller.notifyStarted(Integer.parseInt(nParticles.getText()));
			} else if (e.getSource() == stopButton) {
				setNotRunningConfig();
				controller.notifyStopped();
			}
		}

		private void setNotRunningConfig() {
			startButton.setEnabled(true);
			stopButton.setEnabled(false);
			zoomIn.setEnabled(false);
			zoomOut.setEnabled(false);
		}

		private void setRunningConfig() {
			startButton.setEnabled(false);
			stopButton.setEnabled(true);
			zoomIn.setEnabled(true);
			zoomOut.setEnabled(true);
		}
    

	    class VisualiserPanel extends JPanel implements MouseListener {
	
	    	 	private long dx;
	        private long dy;
	        
	        private StringBuffer timeText; 
	        private Formatter fmt;
	        
	        public VisualiserPanel(int w, int h){
	            setSize(w,h);
	            dx = w/2 - 20;
	            dy = h/2 - 20;
	            this.addMouseListener(this);
	            
	            timeText = new StringBuffer();
	            fmt = new Formatter(timeText);
	        }
	
	        public void paint(Graphics g){
		    		Graphics2D g2 = (Graphics2D) g;
		    		
		    		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		    		          RenderingHints.VALUE_ANTIALIAS_ON);
		    		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
		    		          RenderingHints.VALUE_RENDER_QUALITY);
		    		g2.clearRect(0,0,this.getWidth(),this.getHeight());
		
		    		WorldSnapshot snap = world.getSnapshotToDisplay();
		    		if (snap != null) {
			    		P2d[] currentPosSnapshot = snap.getPosList();
			    		
			    		for (P2d pos: currentPosSnapshot) {	
			    			int x0 = getViewX(pos.x);
					    int y0 = getViewY(pos.y);
					    g2.drawOval(x0,y0,5,5);
			    		}
		    		}
		    		
		    		timeText.setLength(0);
		    		fmt.format("%.2f", world.getCurrentTime());
		    		time.setText(timeText.toString());
	    		}
	        
	        private int getViewX(double x) {
	        		return (int)(dx + x*scale);
	        }
	
	        private int getViewY(double y) {
	    			return (int)(dy - y*scale);
	        }
	
	
			@Override
			public void mousePressed(MouseEvent e) {
				P2d p = new P2d((e.getX() - dx)/scale, (dy - e.getY())/scale);			
				// System.out.println("NEW POINT " + e.getX() + " " + e.getY() + " => " + p);
				notifyNewParticle(p);
			}
	
			@Override
			public void mouseClicked(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
	
			@Override
			public void mouseEntered(MouseEvent e) {}
	
			@Override
			public void mouseExited(MouseEvent e) {}        
	    }
    }    
}