package assignment2.e0;

import assignment2.Document;
import assignment2.DocumentAnalyzer;
import assignment2.DocumentResult;
import assignment2.Utils;

import java.util.concurrent.Callable;

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
        this.updater.submitResult(this.path, DocumentAnalyzer.analyzeDocument(Document.fromPath(this.path)));
    }
}
