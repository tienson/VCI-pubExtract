package main.XMLParser;

import main.Metadata.Author;
import main.Metadata.Metadata;
import main.Metadata.Publisher;
import main.Metadata.Reference;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ExpectedXMLParser implements XMLParser{
    @Override
    public Metadata parse(File XMLFile) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        // Ignore .dtd file
        saxBuilder.setFeature("http://xml.org/sax/features/validation", false);
        saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        saxBuilder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        Document xmlDocument = saxBuilder.build(XMLFile);
        Element root = xmlDocument.getRootElement();
        Metadata metadata = new Metadata();

        metadata.setTitle(root.getChild("title").getValue());
        metadata.setAuthors(parseAuthors(root.getChild("authors")));
        metadata.setPublisher(parsePublisher(root));
        metadata.setAbstract(root.getChild("abstract").getValue());
        metadata.setKeywords(parseKeywords(root.getChild("keywords")));
        metadata.setReferences(parseReferences(root.getChild("references")));

        return metadata;
    }

    public static Author[] parseAuthors(Element authorsElement) {
        List<Element> authorList = authorsElement.getChildren();
        Author[] authors = new Author[authorList.size()];

        for (int i = 0; i < authors.length; i++) {
            authors[i] = new Author();

            authors[i].setName(authorList.get(i).getChild("name").getValue());
//            authors[i].setEmail(authorList.get(i).getChild("email").getValue());
            authors[i].setAffiliation(authorList.get(i).getChild("affiliation").getValue());
//            authors[i].setTel(authorList.get(i).getChild("tel").getValue());
        }

        return authors;
    }

    public static Publisher parsePublisher(Element publisherElement) {
        Publisher publisher = new Publisher();

        publisher.setPaper(publisherElement.getChild("journal").getValue());
        publisher.setVolume(publisherElement.getChild("volume").getValue());
        publisher.setNo(publisherElement.getChild("number").getValue());
        publisher.setYear(publisherElement.getChild("year").getValue());

        return publisher;
    }

    public static String[] parseKeywords(Element keywordsElement) {
        List<Element> keywordList = keywordsElement.getChildren();
        String[] keywords = new String[keywordList.size()];

        for (int i = 0; i < keywords.length; i++) {
            keywords[i] = keywordList.get(i).getValue();
        }

        return keywords;
    }

    private static Reference[] parseReferences(Element referencesElement) {
        List<Element> referenceList = referencesElement.getChildren();
        Reference[] references = new Reference[referenceList.size()];

        for (int i = 0; i < references.length; i++) {
            references[i] = new Reference();
            Element ref = referenceList.get(i).getChild("ref");

//            references[i].setAuthors(parseRefAuthors(ref.getChild("authors")));
//            references[i].setTitle(ref.getChild("title").getValue());
//            references[i].setPublisher(parseRefPublisher(ref));

            references[i].setRaw(ref.getChild("raw").getValue());
        }

        return references;
    }

    private static String[] parseRefAuthors(Element refAuthorsElement) {
        List<Element> refAuthorList = refAuthorsElement.getChildren();
        String[] authors = new String[refAuthorList.size()];

        for (int i = 0; i < authors.length; i++) {
            authors[i] = refAuthorList.get(i).getChild("name").getValue();
        }

        return authors;
    }

    private static Publisher parseRefPublisher(Element refPublisherElement) {
        Publisher publisher = new Publisher();

        publisher.setPaper(refPublisherElement.getChild("paper").getValue());
        publisher.setVolume(refPublisherElement.getChild("volume").getValue());
        publisher.setNo(refPublisherElement.getChild("number").getValue());
        publisher.setYear(refPublisherElement.getChild("year").getValue());

        return publisher;
    }
}
