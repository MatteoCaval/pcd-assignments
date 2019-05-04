package assignment2.e0;

import assignment2.fileanalysis.DocumentAnalyzer;
import assignment2.fileanalysis.DocumentResult;

public class FileComputeTask implements Runnable {

    interface Updater {
        void submitResult(String path, DocumentResult result);
    }

    private String path;
    private Updater updater;


    public FileComputeTask(String path, Updater updater) {
        this.updater = updater;
        this.path = path;
    }


    @Override
    public void run() {
        this.updater.submitResult(this.path, DocumentAnalyzer.resultFromPath(this.path));
    }
}
