package main.Information;

import org.apache.pdfbox.text.TextPosition;
import pl.edu.icm.cermine.ContentExtractor;

/**
 * Created by nghia on 7/10/2017.
 */
public class Word {
    private String word;

    private float positionX;
    private float positionY;

    private float width;
    private float height;

    private String fontName;
    private float fontSize;

    private boolean isBold;
    private boolean isItalic;
    private boolean isUnderline;
    private boolean isCapital;

    // Constructors

    public Word() {
    }

    public Word(String word, float positionX, float positionY, float width, float height, String fontName,
                float fontSize, boolean isBold, boolean isItalic, boolean isUnderline, boolean isCapital) {
        this.word = word;
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.isBold = isBold;
        this.isItalic = isItalic;
        this.isUnderline = isUnderline;
        this.isCapital = isCapital;
    }


    // Getters
    public String getWord() {
        return word;
    }

    public float getPositionX() {
        return positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public String getFontName() {
        return fontName;
    }

    public float getFontSize() {
        return fontSize;
    }

    public boolean isBold() {
        return isBold;
    }

    public boolean isItalic() {
        return isItalic;
    }

    public boolean isUnderline() {
        return isUnderline;
    }

    public boolean isCapital() {
        return isCapital;
    }

    public String toString(){ return getWord();}
}
