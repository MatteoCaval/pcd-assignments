package assignment1.view;

import assignment1.Boundary;
import assignment1.common.P2d;
import assignment1.common.States;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ParticleView extends JFrame implements ActionListener {

    private static final int PARTICLE_DIAMETER = 20;

    private JTextField state;
    private JTextField steps;
    private JTextField particleField;
    private VisualiserPanel panel;
    private InputListener listener;
    private double boundaryWidth;
    private double boundaryHeight;

    public ParticleView(int w, int h) {
        super("Particle Viewer");

        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");
        JPanel controlPanel = new JPanel();
        controlPanel.add(startButton);
        controlPanel.add(stopButton);

        panel = new VisualiserPanel(w, h);
        panel.setPreferredSize(new Dimension(w,h));

        JPanel infoPanel = new JPanel();

        particleField = new JTextField(10);
        particleField.setText("0");
        particleField.setEditable(false);
        infoPanel.add(new JLabel("Particles"));
        infoPanel.add(particleField);

        steps = new JTextField(10);
        steps.setText("0");
        steps.setEditable(false);
        infoPanel.add(new JLabel("Steps"));
        infoPanel.add(steps);

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

        pack(); // setta la misura del frame in base a quella dei vari elem. Così il pannello centrale è delle misure volute.
    }

    public void display() {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(true);
        });
    }


    public void changeState(final States s) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                state.setText(s.getString());
            }
        });
    }

    public void updateSteps(final int step) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                steps.setText(String.valueOf(step));
            }
        });
    }

    public void setPartcilesNumber(final int nParticles) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                particleField.setText(String.valueOf(nParticles));
            }
        });
    }


    @Override
    public void actionPerformed(ActionEvent ev) {
        String cmd = ev.getActionCommand();
        if (cmd.equals("Start")) {
            listener.startPressed();
        } else if (cmd.equals("Stop")) {
            listener.stopPressed();
        }
    }

    public void updatePositions(ArrayList<P2d> array) {
        panel.updatePositions(array);
    }

    public void setInputListener(InputListener listener) {
        this.listener = listener;
    }

    public void setBoundary(Boundary boundary){
        this.boundaryHeight = boundary.getHeight();
        this.boundaryWidth = boundary.getWidth();;
    }

    public class VisualiserPanel extends JPanel {
        private ArrayList<P2d> positions;
        private long panelWidth;
        private long panelHeight;
        private long dx;
        private long dy;


        VisualiserPanel(int w, int h) {
            panelWidth = w;
            panelHeight = h;
            dx = w / 2;
            dy = h / 2;
        }

        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g2.clearRect(0, 0, this.getWidth(), this.getHeight());
            synchronized (this) {
                if (positions != null) {
                    positions.stream().forEach(p -> {
                        int x0 = (int) (dx + p.x * panelWidth /(boundaryWidth + boundaryWidth / 2));
                        int y0 = (int) (dy - p.y * panelHeight /(boundaryHeight + boundaryHeight / 2));
                        g2.drawOval(x0, y0, PARTICLE_DIAMETER, PARTICLE_DIAMETER);
                    });
                }
            }
        }

        private void updatePositions(ArrayList<P2d> pos) {
            synchronized (this) {
                positions = pos;
            }
            repaint();
        }
    }

}
	
