package assignment2;

import javafx.util.Pair;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class View extends JFrame {

    public interface SelectorListener {
        void startPressed(List<String> paths);

        void filesAdded(String... paths);

        void fileRemoved(String path);

        void stopPressed();
    }

    private JButton addDirectoryButton;
    private JButton addFileButton;
    private JFileChooser chooser;
    private JTextField selectedDirectoryName;
    private JButton removeElementButton;
    private JButton startButton;
    private JButton stopButton;
    private DefaultListModel<String> elementListModel;
    private JList<String> elementList;
    private DefaultListModel<String> resultListModel;

    private boolean started = false;
    private SelectorListener listener;

    public View(SelectorListener listener) throws HeadlessException {
        this.listener = listener;
        initUI();
        setVisible(true);
    }

    public void printResult(List<Pair<String, Integer>> result) {
        SwingUtilities.invokeLater(() -> {
            this.resultListModel.clear();
            result.stream().limit(10).forEach(e -> this.resultListModel.addElement(e.getValue().toString() + " - " + e.getKey()));
        });
    }


    private void initUI() {
        setTitle("Assignment2");
        setResizable(true);
        this.setSize(600, 400);


        this.addDirectoryButton = new JButton("Add directory");
        this.addFileButton = new JButton("Add file");
        this.removeElementButton = new JButton("Remove element");
        this.startButton = new JButton("Start");
        this.stopButton = new JButton("Stop");

        JPanel selectionPanel = new JPanel();
        selectionPanel.add(this.addDirectoryButton);
        selectionPanel.add(this.addFileButton);
        selectionPanel.add(this.removeElementButton);

        this.elementListModel = new DefaultListModel<>();
        this.elementList = new JList<>(this.elementListModel);
        JScrollPane listScrollPanel = new JScrollPane(this.elementList);


        JPanel controlPanel = new JPanel();
        controlPanel.add(this.startButton);
        controlPanel.add(this.stopButton);

        this.resultListModel = new DefaultListModel<>();
        JList<String> resultList = new JList<>(this.resultListModel);
        JScrollPane resultListScrollPanel = new JScrollPane(resultList);

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BorderLayout());
        resultPanel.add(BorderLayout.NORTH, resultListScrollPanel);
        resultPanel.add(BorderLayout.SOUTH, controlPanel);


        this.setLayout(new BorderLayout());
        this.add(BorderLayout.NORTH, listScrollPanel);
        this.add(BorderLayout.SOUTH, resultPanel);
        this.add(BorderLayout.CENTER, selectionPanel);


        this.addDirectoryButton.addActionListener(e -> {
            this.selectDirectory();
        });

        this.addFileButton.addActionListener(e -> {
            this.selectFiles();
        });

        this.removeElementButton.addActionListener(e -> {
            int selectedIndex = this.elementList.getSelectedIndex();
            if (selectedIndex >= 0) {
                this.removeSelectedElement(selectedIndex);
            }

        });

        this.startButton.addActionListener(e -> {
            this.started = true;
            this.startButton.setEnabled(false);
            this.stopButton.setEnabled(true);
            this.listener.startPressed(fromListModel(elementListModel));
        });

        this.stopButton.addActionListener(e -> {
            this.started = false;
            this.stopButton.setEnabled(false);
            this.startButton.setEnabled(true);
            this.listener.stopPressed();
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    // region Private methods

    private void selectDirectory() {
        this.chooser = new JFileChooser();
        this.chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
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

