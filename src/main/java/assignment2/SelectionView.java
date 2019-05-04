package assignment2;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SelectionView extends JFrame {

    interface ModeSelectionListener {
        void modeSelected(ControllerEnum mode, boolean parallel);
    }

    private ModeSelectionListener listener;


    public SelectionView(ModeSelectionListener listener) throws HeadlessException {
        this.listener = listener;
        initUI();
        setVisible(true);
    }

    private void initUI() {
        setTitle("Assignment2");
        setResizable(true);
        this.setSize(600, 450);

        JPanel modeSelectorPanel = new JPanel();

        JRadioButton parallelMode = new JRadioButton("Parallel");
        parallelMode.setSelected(true);
        parallelMode.setActionCommand("Parallel");

        JRadioButton sequentialMode = new JRadioButton("Sequential");
        sequentialMode.setActionCommand("Sequential");
        sequentialMode.setSelected(false);

        ButtonGroup group = new ButtonGroup();
        group.add(parallelMode);

        group.add(sequentialMode);

        modeSelectorPanel.add(parallelMode);
        modeSelectorPanel.add(sequentialMode);

        ArrayList<JButton> controllerButtons = new ArrayList<>();

        JPanel controllerSelectorPanel = new JPanel();
        JButton e0Button = new JButton();
        e0Button.setText("Task and execuors");
        controllerButtons.add(e0Button);

        JButton e1Button = new JButton();
        e1Button.setText("Event loop");
        controllerButtons.add(e1Button);

        JButton e2Button = new JButton();
        e2Button.setText("Reactive streams");
        controllerButtons.add(e2Button);

        controllerSelectorPanel.setLayout(new GridLayout(3, 1, 20, 40));

        for (int i = 0; i < controllerButtons.size(); i++) {
            JButton btn = controllerButtons.get(i);
            int count = i;
            controllerButtons.get(i).addActionListener(e -> {
                selectController(count, group.getSelection().getActionCommand());
                dispose();
            });
            controllerSelectorPanel.add(btn);
        }

        this.setLayout(new BorderLayout());
        this.add(BorderLayout.NORTH, modeSelectorPanel);
        this.add(BorderLayout.CENTER, controllerSelectorPanel);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    private void selectController(int index, String parallel) {
        ControllerEnum mode = null;
        switch (index) {
            case 0:
                mode = ControllerEnum.ES0;
                break;
            case 1:
                mode = ControllerEnum.ES1;
                break;
            case 2:
                mode = ControllerEnum.ES2;
                break;
        }

        boolean paral = true;

        if (parallel.equals("Sequential")) {
            paral = false;
        }

        listener.modeSelected(mode, paral);
    }
}

