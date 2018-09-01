package main.XMLParser;

import main.Metadata.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CermineParser implements XMLParser {
    @Override
    public Metadata parse(File XMLFile) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        // Ignore .dtd file
        saxBuilder.setFeature("http://xml.org/sax/features/validation", false);
        saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        Document xmlDocument = saxBuilder.build(XMLFile);
        Metadata metadata = new Metadata();

        // Process front
        Element front = xmlDocument.getRootElement().getChild("front");

        metadata.setTitle(front.getChild("article-meta").getChild("title-group").getChild("article-title").getValue());

        metadata.setAuthors(parseAuthors(front));

        metadata.setPublisher(parsePublisher(front));


        if (front.getChild("article-meta").getChild("abstract") != null) {
            metadata.setAbstract(front.getChild("article-meta").getChild("abstract").getValue());
        }

        metadata.setKeywords(parseKeywords(front));

        // Process back, which is stuffed with references
        Element references = xmlDocument.getRootElement().getChild("back").getChild("ref-list");
        if (references != null) {
            metadata.setReferences(parseReferences(references));
        }

        return metadata;
    }

    private Author[] parseAuthors(Element front) {
        int firstAffIdx = 0;    // The index of the first aff element
        List<Element> contribList = front.getChild("article-meta").getChild("contrib-group").getChildren();    // This list contains both Author and Affiliation
        for (int i = 0; i < contribList.size(); i++) {
            if (contribList.get(i).getName().equals("aff")) {
                firstAffIdx = i;
                break;
            }
        }

        Author[] authors = new Author[firstAffIdx];
        int affIdx;
        for (int i = 0; i < firstAffIdx; i++) {
            authors[i] = new Author();

            if (contribList.get(i).getChild("string-name") != null) {
                authors[i].setName(contribList.get(i).getChild("string-name").getText());
            }

            if (contribList.get(i).getChild("email") != null) {
                authors[i].setEmail(contribList.get(i).getChild("email").getText());
            }

            if (contribList.get(i).getChild("xref") != null) {
                affIdx = firstAffIdx + Integer.parseInt(contribList.get(i).getChild("xref").getText());
                authors[i].setAffiliation(contribList.get(affIdx).getChild("institution").getText());
            }
        }

        return authors;
    }

    private Publisher parsePublisher(Element front) {
        Publisher publisher = new Publisher();

        try {
            publisher.setPaper(front.getChild("journal-meta").getChild("journal-title-group")
                                    .getChild("journal-title").getValue());
        } catch (Exception e) {
            
        }

        Element article_meta = front.getChild("article-meta");

        if (article_meta.getChild("volume") != null) {
            publisher.setVolume(article_meta.getChild("volume").getValue());
        }

        if (article_meta.getChild("pub-date") != null && article_meta.getChild("pub-date").getChild("year") != null) {
            publisher.setYear(article_meta.getChild("pub-date").getChild("year").getValue());
        }

        if (article_meta.getChild("fpage") != null) {
            publisher.setPageStart(article_meta.getChild("fpage").getValue());
        }

        if (article_meta.getChild("lpage") != null) {
            publisher.setPageEnd(article_meta.getChild("lpage").getValue());
        }

        if (article_meta.getChild("history") != null) {
            List<Element> dateList = article_meta.getChild("history").getChildren();
            Date date;

            for (Element dateElement : dateList) {
                date = new Date();

                if (dateElement.getChild("day") != null) {
                    date.setDay(dateElement.getChild("day").getValue());
                }

                if (dateElement.getChild("month") != null) {
                    date.setMonth(dateElement.getChild("month").getValue());
                }

                if (dateElement.getChild("year") != null) {
                    date.setYear((dateElement.getChild("year").getValue()));
                }

                switch (dateElement.getAttributes().get(0).getValue()) {
                    case "received":
                        publisher.setReceived(date);
                        break;

                    case "revised":
                        publisher.setRevised(date);
                        break;

                    default:
                        publisher.setAccepted(date);
                }
            }

        }

        return publisher;
    }

    private String[] parseKeywords(Element front) {
        if (front.getChild("article-meta").getChild("kwd-group") != null) {
            List<Element> keywordList = front.getChild("article-meta").getChild("kwd-group").getChildren();
            String[] keywords = new String[keywordList.size()];

            for (int i = 0; i < keywords.length; i++) {
                keywords[i] = keywordList.get(i).getValue();
            }

            return keywords;
        } else {
            return new String[0];
        }
    }

    private Reference[] parseReferences(Element refElement) {
        List<Element> referencesList = refElement.getChildren();
        Reference[] references = new Reference[referencesList.size()];

        for (int i = 0; i < references.length; i++) {
            references[i] = new Reference();
            Element reference = referencesList.get(i).getChildren().get(0);    // mixed-citation

            references[i].setAuthors(parseRefAuthor(reference));

            if (reference.getChild("article-title") != null) {
                references[i].setTitle(reference.getChild("article-title").getValue());
            }

            references[i].setPublisher(parseRefPublisher(reference));
        }

        return references;
    }

    private String[] parseRefAuthor(Element refElement) {
        List<String> authorList = new ArrayList<>();

        for (Element temp : refElement.getChildren()) {
            if (temp.getName().equals("string-name")) {
                List<Element> nameElement = temp.getChildren();

                String name;
                if (nameElement.size() == 1) {
                    name = nameElement.get(0).getValue();
                } else {
                    name = nameElement.get(0).getValue() + ", " + nameElement.get(1).getValue();
                }

                authorList.add(name);
            }
        }

        return authorList.toArray(new String[authorList.size()]);
    }

    private Publisher parseRefPublisher(Element reference) {
        Publisher publisher = new Publisher();

        if (reference.getChild("source") != null) {
            publisher.setPaper(reference.getChild("source").getValue());
        }

        if (reference.getChild("volume") != null) {
            publisher.setVolume(reference.getChild("volume").getValue());
        }

        if (reference.getChild("year") != null) {
            publisher.setYear(reference.getChild("year").getValue());
        }

        if (reference.getChild("issue") != null) {
            publisher.setNo(reference.getChild("issue").getValue());
        }

        if (reference.getChild("fpage") != null) {
            publisher.setPageStart(reference.getChild("fpage").getValue());
        }

        if (reference.getChild("lpage") != null) {
            publisher.setPageEnd(reference.getChild("lpage").getValue());
        }

        return publisher;
    }
}
