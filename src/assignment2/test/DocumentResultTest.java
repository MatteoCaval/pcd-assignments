package assignment2.test;

import assignment2.DocumentResult;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

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
}