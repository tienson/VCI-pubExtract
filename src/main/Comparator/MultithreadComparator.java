package main.Comparator;

import main.Metadata.Metadata;
import main.XMLParser.CermineParser;
import main.XMLParser.ExpectedXMLParser;
import main.XMLParser.XMLParser;

import java.io.File;

public class MultithreadComparator implements Runnable {
    private File testXML;
    private String expectedXMLFolder;
    private Comparator.XMLType xmlType;
    private float[] resultsArray;

    public MultithreadComparator(File testXML, Comparator.XMLType xmlType, String expectedXMLFolder, float[] resultsArray) {
        this.testXML = testXML;
        this.expectedXMLFolder = expectedXMLFolder;
        this.xmlType = xmlType;
        this.resultsArray = resultsArray;
    }

    @Override
    public void run() {
        if (xmlType == Comparator.XMLType.CERMINE) {
            XMLParser testParser = new CermineParser();
            XMLParser expectedParser = new ExpectedXMLParser();
            try {
                Metadata test = testParser.parse(testXML);

                File expectedXML = new File(expectedXMLFolder + "\\" + testXML.getName().substring(0, testXML.getName().lastIndexOf('.')) + ".xml");
                Metadata expected = expectedParser.parse(expectedXML);

                float[] results = Comparator.compare(test, expected);
                StringBuilder builder = new StringBuilder("");

                builder.append(String.format("%1$13s", testXML.getName())).append("    ");    // xml filename with spaces
                for (int i = 0; i < 6; i++) {
                    builder.append(String.format("%.5f", results[i])).append("    ");
                }

                builder.append(Comparator.normalize(test.getTitle())).append("    -    ").append(Comparator.normalize(expected.getTitle()));
                System.out.println(builder.toString());

                for (int i = 0; i < 6; i++) {
                    resultsArray[i] = results[i];
                }
            } catch (Exception e) {
                System.out.println(testXML.getName() + " fails");
                e.printStackTrace();
            }
        }
    }
}
