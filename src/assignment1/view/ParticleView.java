package assignment1.view;

import assignment1.common.P2d;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

public class ParticleView extends JFrame implements ActionListener{

    private static final int PARTICLE_DIAMETER = 20;

    private JButton startButton;
    private JButton stopButton;
    private JTextField state;
    private ArrayList<InputListener> listeners;
    private VisualiserPanel panel;

    public ParticleView(int w, int h){
        super("Mandelbrot Viewer");
        setSize(w, h);
        listeners = new ArrayList<InputListener>();

        startButton = new JButton("start");
        stopButton = new JButton("stop");
        JPanel controlPanel = new JPanel();
        controlPanel.add(startButton);
        controlPanel.add(stopButton);

        panel = new VisualiserPanel(w, h);

        JPanel infoPanel = new JPanel();
        state = new JTextField(20);
        state.setText("Idle");
        state.setEditable(false);
        infoPanel.add(new JLabel("State"));
        infoPanel.add(state);
        JPanel cp = new JPanel();
        LayoutManager layout = new BorderLayout();
        cp.setLayout(layout);
        cp.add(BorderLayout.NORTH, controlPanel);
        cp.add(BorderLayout.CENTER, panel);
        cp.add(BorderLayout.SOUTH, infoPanel);
        setContentPane(cp);

        startButton.addActionListener(this);
        stopButton.addActionListener(this);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void display(){
        SwingUtilities.invokeLater(() -> {
            this.setVisible(true);
        });
    }


    public void changeState(final String s){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                state.setText(s);
            }
        });
    }

    public void addListener(InputListener l){
        listeners.add(l);
    }


    private void notifyStopped(){
        for(InputListener l : listeners){
            l.stopped();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){

    }

    public void updatePositions(P2d[] array){
        panel.updatePositions(array);
    }

    public class VisualiserPanel extends JPanel{
        private P2d[] positions;
        private long dx;
        private long dy;

        public VisualiserPanel(int w, int h){
            setSize(w, h);
            dx = w / 2 - PARTICLE_DIAMETER;
            dy = h / 2 - PARTICLE_DIAMETER;
        }

        public void paint(Graphics g){
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g2.clearRect(0, 0, this.getWidth(), this.getHeight());
            synchronized(this){
                if(positions != null){
                    Arrays.stream(positions).forEach(p -> {
                        int x0 = (int) (dx + p.x * dx);
                        int y0 = (int) (dy - p.y * dy);
                        g2.drawOval(x0, y0, PARTICLE_DIAMETER, PARTICLE_DIAMETER);
                    });
                }
                g2.drawString("Particles: " + positions.length, 2, PARTICLE_DIAMETER);

            }

        }

        public void updatePositions(P2d[] pos){
            synchronized(this){
                positions = pos;
            }
            repaint();
        }
    }

}
	
