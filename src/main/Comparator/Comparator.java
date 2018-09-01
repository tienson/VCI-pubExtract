package main.Comparator;

import main.Metadata.Author;
import main.Metadata.Metadata;
import main.Metadata.Publisher;
import main.Metadata.Reference;
import main.XMLParser.CermineParser;
import main.XMLParser.ExpectedXMLParser;
import main.XMLParser.XMLParser;
import org.apache.xmlbeans.impl.common.Levenshtein;
import org.jdom2.JDOMException;

import java.io.*;
import java.lang.reflect.Array;
import java.text.Normalizer;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Comparator {
    public enum XMLType {
        CERMINE
    }

    public static final int TITLE = 0;
    public static final int AUTHORS = 1;
    public static final int PUBLISHER = 2;
    public static final int ABSTRACT = 3;
    public static final int KEYWORDS = 4;
    public static final int REFERENCES = 5;

    /**
     * The AIO comparision function. Put things here and see the magic works
     *
     * @param testFolder
     * @param testType
     * @param expectedFolderPath
     * @throws JDOMException
     * @throws IOException
     */
    public static void compare(File testFolder, XMLType testType, String expectedFolderPath) throws JDOMException, IOException {
        Collection<Future<?>> tasks = new LinkedList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        float[][] resultsArray = new float[testFolder.listFiles().length][6];

        Future<?> future;
        ArrayList<String> processedFile = new ArrayList<>();
        for (File testXMLFile : testFolder.listFiles()) {
            if (testXMLFile.getName().endsWith(".cermxml")) {
                future = executorService.submit(new MultithreadComparator(testXMLFile, testType,
                                                expectedFolderPath, resultsArray[processedFile.size()]));
                processedFile.add(testXMLFile.getName());
                tasks.add(future);
            }
        }

        for (Future<?> currTask : tasks) {
            try {
                currTask.get();
            } catch (Throwable thrown) {}
        }
        executorService.shutdown();

        System.out.println("Processed " + processedFile.size() + " file(s)");
        System.out.println("Writing results...");
        File outputCSV = new File(testFolder.getPath() + "\\results.csv");
        writeToCSV(resultsArray, processedFile, outputCSV);

        System.out.println("IT SHUT FUCKING DOWN");
    }

    /**
     * Compare directly 2 parsed Metadata.
     * The comparisons generate a float number between 0 and 1 to illustrate the similarity of the strings,
     * with 0 means total difference and 1 means that they are identical
     * All strings are normalized before compared.
     *
     * @param test
     * @param expected
     * @return An array of the results, which has 6 elements corresponding to the 6 comparison
     */
    public static float[] compare(Metadata test, Metadata expected) {
        float[] results = new float[6];

        results[TITLE] = similarity(test.getTitle(), expected.getTitle());

        int numOfAuthors = Math.min(test.getAuthors().length, expected.getAuthors().length);
        for (int i = 0; i < numOfAuthors; i++) {
            results[AUTHORS] = results[AUTHORS] + similarity(test.getAuthors()[i].getName(), expected.getAuthors()[i].getName());
            results[AUTHORS] = results[AUTHORS] + similarity(test.getAuthors()[i].getAffiliation(), expected.getAuthors()[i].getAffiliation());
        }
        results[AUTHORS] = numOfAuthors == 0 ? 0 : results[AUTHORS] / (numOfAuthors * 2);

        results[PUBLISHER] = results[PUBLISHER] + similarity(test.getPublisher().getPaper(), expected.getPublisher().getPaper());
        results[PUBLISHER] = results[PUBLISHER] + similarity(test.getPublisher().getVolume(), expected.getPublisher().getVolume());
        results[PUBLISHER] = results[PUBLISHER] + similarity(test.getPublisher().getYear(), expected.getPublisher().getYear());
        results[PUBLISHER] = results[PUBLISHER] / 3;

        results[ABSTRACT] = similarity(test.getAbstract(), expected.getAbstract());

        int numOfKeyword = Math.min(test.getKeywords().length, expected.getKeywords().length);
        for (int i = numOfKeyword - 1; i >= 0; i--) {
            results[KEYWORDS] = results[KEYWORDS] + similarity(test.getKeywords()[i], expected.getKeywords()[i]);
        }
        results[KEYWORDS] = numOfKeyword == 0 ? 0 : results[KEYWORDS] / numOfKeyword;

        int numOfReference = Math.min(test.getReferences().length, expected.getReferences().length);
        for (int i = 0; i < numOfReference; i++) {
            results[REFERENCES] = results[REFERENCES] + similarity(test.getReferences()[i].toString(), expected.getReferences()[i].toString());
        }
        results[REFERENCES] = numOfReference == 0 ? 0 : results[REFERENCES] / numOfReference;

        return results;
    }

    /**
     * Remove all umlauts, accents, spaces and punctuations
     *
     * @param input the string to be normalized
     * @return the normalized string
     */
    public static String normalize(String input) {
        char[] out = new char[input.length()];
        input = Normalizer.normalize(input, Normalizer.Form.NFD);
        int j = 0;
        for (int i = 0, n = input.length(); i < n; ++i) {
            char c = input.charAt(i);
            if (Character.isAlphabetic(c) || (c != ' ' && c <= '\u007F' && c != '\n' && c != '\t')) {
                out[j++] = Character.toLowerCase(c);
            }
        }

        return new String(out);
    }

    /**
     * Return the similarity (Levenshtein distance) between 2 strings
     *
     * @param s1
     * @param s2
     * @return The Levenshtein distance of 2 string, divided to the length of the longer string
     *         Of course when both string's lengths are zero, the returned value is 0
     */
    public static float similarity(String s1, String s2) {
        s1 = normalize(s1);
        s2 = normalize(s2);
        int length = Math.max(s1.length(), s2.length());

        return length == 0 ? 0 : 1 - Levenshtein.distance(s1, s2) / length;
    }

    /**
     * Write the results to a csv file for later processing
     *
     * @param resultsArray the results of the comparisons
     * @param processedFiles the name of the compared file (test folder)
     * @param outputCSV the desired output
     */
    public static void writeToCSV(float[][] resultsArray, ArrayList<String> processedFiles, File outputCSV) {
        StringBuilder builder = new StringBuilder("");

        for (int i = 0; i < processedFiles.size(); i++) {
            builder.append(processedFiles.get(i)).append(',');
            for (int j = 0; j < 6; j++) {
                builder.append(String.valueOf(resultsArray[i][j])).append(',');
            }
            builder.append('\n');
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputCSV))) {    // try-with-resource like a pro
            writer.write(builder.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof IOException) {
                System.out.println("sumting wong");
            }
        }
    }
}
