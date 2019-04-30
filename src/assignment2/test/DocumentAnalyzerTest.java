package assignment2.test;

import assignment2.Document;
import assignment2.DocumentAnalyzer;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DocumentAnalyzerTest {

    private Document document;

    @Before
    public void setUp() throws Exception {
        List<String> lines = new ArrayList<>();
        lines.add("ciao io mi chiamo matteo");
        lines.add("ciao che bella giornata oggi");
        this.document = new Document(lines);
    }

    @Test
    public void printWords() {
        System.out.println(this.document.toString());
        System.out.println();
        System.out.println(DocumentAnalyzer.analyzeDocument(this.document).toString());
    }
}