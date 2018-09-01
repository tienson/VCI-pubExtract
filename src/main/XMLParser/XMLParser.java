package main.XMLParser;

import main.Metadata.Metadata;
import org.jdom2.JDOMException;

import java.io.File;
import java.io.IOException;

public interface XMLParser {
    Metadata parse(File XMLDoc) throws JDOMException, IOException;
}
