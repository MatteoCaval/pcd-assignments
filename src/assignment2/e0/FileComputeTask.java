package assignment2.e0;

import assignment2.Document;
import assignment2.DocumentAnalyzer;
import assignment2.DocumentResult;

import java.util.concurrent.Callable;

public class FileComputeTask implements Callable<DocumentResult> {

    private String path;

    public FileComputeTask(String path) {
        this.path = path;
    }

    @Override
    public DocumentResult call() throws Exception {
        return DocumentAnalyzer.analyzeDocument(Document.fromPath(this.path));
    }
}
