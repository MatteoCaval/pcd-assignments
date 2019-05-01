package assignment2.test;

import assignment2.Document;
import assignment2.DocumentResult;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class DocumentResultTest {

    private DocumentResult documentResult;

    @Before
    public void setUp() throws Exception {
        documentResult = new DocumentResult();
    }

    @Test
    public void countingOk() {
        documentResult.insert("prova");
        documentResult.insert("ciao");
        documentResult.insert("ciao");
        assertTrue(documentResult.getResult().get("ciao").equals(2));
    }


    @Test
    public void insertionOk() {
        documentResult.insert("prova");
        documentResult.insert("ciao");
        documentResult.insert("ciao");
        System.out.println(Arrays.toString(documentResult.getResult().entrySet().toArray()));
    }

    @Test
    public void merge() {
        DocumentResult d1 = new DocumentResult();
        DocumentResult d2 = new DocumentResult();
        final String prova = "prova";
        final String ciao = "ciao";

        d1.insert(ciao);
        d1.insert(ciao);
        d1.insert(ciao);
        d1.insert(prova);

        d2.insert(prova);
        d2.insert(prova);
        d2.insert(prova);
        d2.insert(prova);
        d2.insert("colpa");

        DocumentResult merged = DocumentResult.merge(d1, d2);
        System.out.println(merged);


        System.out.println("Lista ordinata di pair:");
        List<Pair<String, Integer>> list = merged.toSortedPair();
        String text = "";
        for (Pair<String, Integer> pair : list) {
            text += pair.getKey() + " -> " + pair.getValue() + "\n";
        }
        System.out.println(text);

    }

}