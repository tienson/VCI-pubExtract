package main.Metadata;

import java.util.Arrays;
import java.util.Comparator;

public class Metadata {
    private String title = "";

    private Author[] authors = new Author[0];

    private Publisher publisher = new Publisher();

    private String abstract_ = "";
    private String[] keywords = new String[0];

    private Reference[] references = new Reference[0];

    // Default constructor
    public Metadata() {

    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author[] getAuthors() {
        return authors;
    }

    public void setAuthors(Author[] authors) {
        this.authors = authors;
        Arrays.sort(this.authors, new Comparator<Author>() {
            @Override
            public int compare(Author o1, Author o2) {
                return o2.getName().compareToIgnoreCase(o1.getName());
            }
        });
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public String getAbstract() {
        return abstract_;
    }

    public void setAbstract(String abstract_) {
        this.abstract_ = abstract_;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    public Reference[] getReferences() {
        return references;
    }

    public void setReferences(Reference[] references) {
        this.references = references;
    }
}
