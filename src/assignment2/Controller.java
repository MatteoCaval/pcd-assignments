package assignment2;

import java.util.List;
import java.util.stream.Collectors;

public class Controller implements View.SelectorListener {

    public void start() {
        new View(this);
    }

    @Override
    public void startPressed(List<String> paths) {
        List<DocumentResult> results = paths.stream().map(p -> DocumentAnalyzer.analyzeDocument(Document.fromPath(p))).collect(Collectors.toList());
        System.out.println(results.stream().reduce(DocumentResult::merge).get().toSortedPair().get(0));

    }
}
