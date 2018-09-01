package main.Information;

import java.util.ArrayList;

/**
 * In this implementation, the string only has lines
 *
 * Created by nghia on 7/10/2017.
 */
public class Page {
    private int pageNumber;
    private float height;
    private float width;

    ArrayList<Line> pageLines;

    // Constructors
    public Page() {
    }

    public Page(int pageNumber, ArrayList<Line> pageLines, float height, float width) {
        this.pageNumber = pageNumber;
        this.height = height;
        this.width = width;

        this.pageLines = pageLines;
    }

    // Getters

    public int getPageNumber() {
        return pageNumber;
    }

    public ArrayList<Line> getPageLines() {
        return pageLines;
    }

    public Line getLineAt(int lineNumber) {
        return pageLines.get(lineNumber);
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }
}
