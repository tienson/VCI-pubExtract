package main.Demo;

import main.Information.MyDocument;
import main.Information.Line;
import main.Information.Page;
import main.Information.Word;
import main.LegacyFontConverter.ConverterFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nghia on 6/24/2017.
 */

public class StyleExtractor extends PDFTextStripper {

    private PDDocument document;

    private ArrayList<Line> allLines = new ArrayList<>();
    private ArrayList<Page> allPages = new ArrayList<>();
    private MyDocument finalDocument;

    private String content;    // The whole content of the file

    private boolean startOfLine = true;    // Mark the start of each line

    private ArrayList<Word> currentLineWord = new ArrayList<>();

    private int pageCounter = 0;

    private ConverterFactory converterFactory = new ConverterFactory();

    /**
     * Instantiate a new PDFTextStripper object.
     * Universal function: it does everything to initialize all important variable of the class
     *
     * @throws IOException If there is an error loading the properties.
     */

    public StyleExtractor(PDDocument pdfDocument) throws IOException {
        this.document = pdfDocument;

        // Trigger all functions to make the finalDocument
        content = super.getText(this.document);
        finalDocument = new MyDocument(document, allPages);
    }

    public ArrayList<Page> getAllPages() {
        return allPages;
    }

    public MyDocument getDocument() {
        return finalDocument;
    }

    public String getContent() {
        return content;
    }

    @Override
    protected void writeLineSeparator() throws IOException {
        startOfLine = true;

        super.writeLineSeparator();
    }

    @Override
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
        if (startOfLine) {
            if (currentLineWord.size() != 0) {    // Add existing line
                allLines.add(new Line(currentLineWord));
            }

            currentLineWord = new ArrayList<>();
            startOfLine = false;    // wait for next startOfLine
        }
        currentLineWord.addAll(splitWords(textPositions));

        super.writeString(text, textPositions);
    }

    @Override
    protected void endPage(PDPage page) throws IOException {
        if (currentLineWord.size() != 0) {    // Fix the lost last line
            allLines.add(new Line(currentLineWord));
            currentLineWord = new ArrayList<>();
        }

        // Add new page
        PDRectangle tempMediaBox = document.getPage(pageCounter).getMediaBox();
        allPages.add(new Page(pageCounter, allLines, tempMediaBox.getHeight(), tempMediaBox.getWidth()));

        // Prepare for new page
        allLines = new ArrayList<>();
        startOfLine = true;
        pageCounter++;

        super.endPage(page);
    }



    /**
     * Split TextPosition of a line into an ArrayList of Word
     *
     * @param textPositions of a line
     * @return list of corresponding Word
     */
    private ArrayList<Word> splitWords(List<TextPosition> textPositions) {

        ArrayList<TextPosition> wordTextPositions = new ArrayList<>();

        ArrayList<Word> lineWords = new ArrayList<>();

        for (TextPosition textPosition : textPositions) {
            char character = textPosition.getUnicode().charAt(0);    // Many TextPosition can have multiple characters to handle localized input method: VNI,...

            // Stupid non-breaking space!
            // Size of the current word must be larger than 0 to prevent multiple spaces
            if (character == ' ' || character == ' ' || character == '\t') {
                if (wordTextPositions.size() != 0) {
                    lineWords.add(formWord(wordTextPositions));
                }

                wordTextPositions = new ArrayList<>();    // Prepare for a new word
            } else {
                wordTextPositions.add(textPosition);
            }
        }

        if (wordTextPositions.size() != 0) {    // Add the last word, if any.
            lineWords.add(formWord(wordTextPositions));
        }

        if (lineWords.size() == 0) {    // The string is full of invisible characters. At least add something in
            lineWords.add(formWord((ArrayList<TextPosition>) textPositions));
        }

        return lineWords;


    }

    /**
     * Form a word from the corresponding list of TextPositions
     *
     * @param wordTextPositions of all words in a line
     * @return Formed word
     */
    private Word formWord(ArrayList<TextPosition> wordTextPositions) {
        // Find the highest character and its position in the wordTextPosition.
        // This character is important because its positionY will be the positionY of the whole string.
        int indexOfHighestCharacter = 0;
        float maxHeight = wordTextPositions.get(0).getHeight();
        for (int i = 1; i < wordTextPositions.size(); i++) {
            if (maxHeight < wordTextPositions.get(i).getHeight()) {
                maxHeight = wordTextPositions.get(i).getHeight();
                indexOfHighestCharacter = i;
            }
        }

        // Create word
        StringBuilder builder = new StringBuilder("");
        for (TextPosition textPosition : wordTextPositions) {
            builder.append(textPosition.getUnicode());
        }
        String word = builder.toString();

        // Handle asshole fonts
        String fontName = wordTextPositions.get(indexOfHighestCharacter).getFont().getName();
        if (fontName.contains("VNI")) {    // The jerk VNI
            word = converterFactory.getVNIConverter().convert(word);
        } else if (fontName.contains("Vn")) {    // The notorious TCVN3
            word = converterFactory.getTCVN3Converter().convert(word);
        }

        String fontNameLowerCase = fontName.toLowerCase();
        boolean isBold = fontNameLowerCase.contains("bold");
        boolean isItalic = fontNameLowerCase.contains("italic");
        boolean isUnderline = fontNameLowerCase.contains("underline");
        boolean isCapital = checkCapital(word);

        float fontSize = wordTextPositions.get(0).getFontSize();

        float positionX =  wordTextPositions.get(0).getX();
        float positionY = wordTextPositions.get(indexOfHighestCharacter).getY();

        float width = wordTextPositions.get(wordTextPositions.size() - 1).getEndX() - wordTextPositions.get(0).getX();
        float height = maxHeight;

        return new Word(word, positionX, positionY, width, height, fontName, fontSize, isBold, isItalic, isUnderline, isCapital);
    }

    /**
     * Check if the input string has at least 80% of its characters capitalized
     *
     * @param word the word to be checked
     * @return The boolean value of the test
     */
    private boolean checkCapital(String word) {
        int counter = 0;

        for(int i = 0; i < word.length(); i++) {
            if (Character.isUpperCase(word.charAt(i))) {
                counter++;
            }
        }

        return counter >= 0.8f * word.length();
    }

    /**
     * Check if the current line is empty/invisible.
     *
     * @param currentLineWord the word to be checked
     * @return the boolean result of the test
     */
    public static boolean checkLine(ArrayList<Word> currentLineWord) {
        if (currentLineWord.size() == 0) {
            return false;
        } else {    // What if the line only has invisible character?
            for (Word word : currentLineWord) {
                String wordString = word.getWord();
                int length = wordString.length();

                for (int i = 0; i < length; i++) {
                    char character = wordString.charAt(i);
                    if (character != ' ' && character != ' ' && character != '\t') {
                        return true;
                    }
                }
            }

            return false;
        }
    }
}
