package main.Information;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by nghia on 7/10/2017.
 */
public class MyDocument {
    private PDDocument document;
    private ArrayList<Page> documentPages = new ArrayList<>();
    private ArrayList<Paragraph> documentParagraphs = new ArrayList<>();

    // Constructors
    public MyDocument() {
    }

    public MyDocument(PDDocument document, ArrayList<Page> documentPages) {
        this.document = document;
        this.documentPages = documentPages;
    }
    public void sortParagraph(){
        Collections.sort(documentParagraphs);
    }
    // Getters
    public PDDocument getDocument() {
        return document;
    }

    public ArrayList<Page> getDocumentPages() {
        return documentPages;
    }


    public Page getPageAt(int pageNumber) {
        return documentPages.get(pageNumber);
    }

    public ArrayList<Paragraph> getDocumentParagraphs() {
        return documentParagraphs;
    }

    public void addParagraph(Paragraph p) {
        documentParagraphs.add(p);
    }
}
