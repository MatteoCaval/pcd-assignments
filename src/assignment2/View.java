package assignment2;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class View extends JFrame {

    interface SelectorListener {
        void start(List<String> paths);
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

    private SelectorListener listener;

    public View(SelectorListener listener) throws HeadlessException {
        this.listener = listener;
        initUI();
        setVisible(true);
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

        JPanel controlPanel = new JPanel();
        controlPanel.add(this.startButton);
        controlPanel.add(this.stopButton);


        this.elementListModel = new DefaultListModel<>();
        this.elementList = new JList<>(this.elementListModel);
        JScrollPane listScrollPanel = new JScrollPane(this.elementList);

        this.selectedDirectoryName = new JTextField("prova");

        LayoutManager mainLayout = new BorderLayout();
        this.setLayout(mainLayout);
        this.add(BorderLayout.NORTH, selectionPanel);
        this.add(BorderLayout.SOUTH, controlPanel);
        this.add(BorderLayout.CENTER, listScrollPanel);


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
        
        this.startButton.addActionListener(e -> this.listener.start(fromListModel(elementListModel)));

    }

    // region Private methods

    private void selectDirectory() {
        this.chooser = new JFileChooser();
        this.chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
//            this.listener.directorySelected(chooser.getSelectedFile());
            try {
                this.addFilesToList(getDirectoryFiles(chooser.getSelectedFile().getPath()));
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

