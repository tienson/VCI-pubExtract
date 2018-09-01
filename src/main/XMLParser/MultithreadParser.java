package main.XMLParser;

import main.Comparator.Comparator;
import main.Metadata.Metadata;
import org.jdom2.JDOMException;

import java.io.File;
import java.io.IOException;

public class MultithreadParser implements Runnable {
    private File xmlFile;
    private Comparator.XMLType xmlType;

    public MultithreadParser(File xmlFile, Comparator.XMLType xmlType) {
        this.xmlFile = xmlFile;
        this.xmlType = xmlType;
    }

    @Override
    public void run() {
        if (xmlType == Comparator.XMLType.CERMINE) {
            XMLParser parser = new CermineParser();
            try {
                Metadata metadata = parser.parse(xmlFile);

                System.out.print(xmlFile.getName());
                System.out.println("    " + metadata.getTitle());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
