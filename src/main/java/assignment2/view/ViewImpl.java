package assignment2.view;

import assignment2.Utils;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ViewImpl extends JFrame implements MainView {

    private JButton startButton;
    private JButton stopButton;
    private JLabel timeSpent;
    private DefaultListModel<String> elementListModel;
    private JList<String> elementList;
    private DefaultListModel<String> resultListModel;

    private boolean started = false;
    private SelectorListener listener;

    public ViewImpl() throws HeadlessException {
        initUI();
        setVisible(true);
    }

    // region ViewInterface

    @Override
    public int getInputSize() {
        return this.elementListModel.size();
    }

    @Override
    public void printResult(List<Pair<String, Integer>> result) {
        SwingUtilities.invokeLater(() -> {
//            Utils.log("Printing results");
            this.resultListModel.clear();
            if (result != null && !result.isEmpty()) {
                result.stream().limit(10).forEach(e -> this.resultListModel.addElement(e.getValue().toString() + " - " + e.getKey()));
            }
        });
    }

    @Override
    public void notifyComputationCompleted() {
        this.stopButtonPressed();
    }

    @Override
    public void setComputationTime(long time) {
        this.timeSpent.setText("Computation time (ms): " + String.valueOf(time));
    }

    @Override
    public void clearComputationTime() {
        this.timeSpent.setText("");
    }

    @Override
    public void setListener(SelectorListener listener) {
        this.listener = listener;
    }

    @Override
    public void setFrameTitle(String title) {
        this.setTitle(title);
    }

    // endregion

    // region Private methods

    private void initUI() {
        setTitle("Assignment 2");
        setResizable(true);
        this.setSize(600, 450);


        JButton addDirectoryButton = new JButton("Add directory");
        JButton addFileButton = new JButton("Add file");
        JButton removeElementButton = new JButton("Remove element");
        this.startButton = new JButton("Start");
        this.stopButton = new JButton("Stop");
        this.timeSpent = new JLabel("");

        JPanel selectionPanel = new JPanel();
        selectionPanel.add(addDirectoryButton);
        selectionPanel.add(addFileButton);
        selectionPanel.add(removeElementButton);

        this.elementListModel = new DefaultListModel<>();
        this.elementList = new JList<>(this.elementListModel);
        JScrollPane listScrollPanel = new JScrollPane(this.elementList);


        JPanel controlPanel = new JPanel();
        controlPanel.add(this.startButton);
        controlPanel.add(this.stopButton);

        JPanel timeResultPanel = new JPanel();
        timeResultPanel.add(this.timeSpent);

        this.resultListModel = new DefaultListModel<>();
        JList<String> resultList = new JList<>(this.resultListModel);
        JScrollPane resultListScrollPanel = new JScrollPane(resultList);

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BorderLayout());
        resultPanel.add(BorderLayout.NORTH, resultListScrollPanel);
        resultPanel.add(BorderLayout.CENTER, controlPanel);
        resultPanel.add(BorderLayout.SOUTH, timeResultPanel);


        this.setLayout(new BorderLayout());
        this.add(BorderLayout.NORTH, listScrollPanel);
        this.add(BorderLayout.SOUTH, resultPanel);
        this.add(BorderLayout.CENTER, selectionPanel);


        addDirectoryButton.addActionListener(e -> {
            this.selectDirectory();
        });

        addFileButton.addActionListener(e -> {
            this.selectFiles();
        });

        removeElementButton.addActionListener(e -> {
            int selectedIndex = this.elementList.getSelectedIndex();
            if (selectedIndex >= 0) {
                this.removeSelectedElement(selectedIndex);
            }

        });

        this.startButton.addActionListener(e -> {
            this.started = true;
            this.startButton.setEnabled(false);
            this.stopButton.setEnabled(true);
            this.resultListModel.clear();
            this.listener.startPressed(fromListModel(elementListModel));
        });

        this.stopButton.addActionListener(e -> {
            this.stopButtonPressed();
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    private void stopButtonPressed() {
        this.started = false;
        this.stopButton.setEnabled(false);
        this.startButton.setEnabled(true);
        this.listener.stopPressed();
    }

    private void selectDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                List<String> paths = getDirectoryFiles(chooser.getSelectedFile().getPath());
                this.addFilesToList(paths);
                if (started) {
                    this.listener.filesAdded(paths.toArray(new String[paths.size()]));
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void selectFiles() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
//            this.listener.directorySelected(chooser.getSelectedFile());
            this.elementListModel.add(this.elementListModel.size(), chooser.getSelectedFile().toString());
            if (started) {
                this.listener.filesAdded(chooser.getSelectedFile().toString());
            }
        }
    }

    private void removeSelectedElement(int selectedIndex) {
        String elemPathToBeRemoved = this.elementListModel.get(selectedIndex);
        this.listener.fileRemoved(elemPathToBeRemoved);
        this.elementListModel.remove(selectedIndex);
    }

    private void addFilesToList(List<String> paths) {
        paths.stream().forEach(p ->
                this.elementListModel.add(this.elementListModel.size(), p)
        );
    }

    private List<String> getDirectoryFiles(String path) throws IOException {
        return Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .map(f -> f.toAbsolutePath().toString())
                .collect(Collectors.toList());

    }

    private List<String> fromListModel(DefaultListModel<String> model) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < model.size(); i++) {
            list.add(model.get(i));
        }
        return list;
    }

    // endregion

}

