package main.Information;

import java.util.ArrayList;

/**
 * Created by nghia on 7/10/2017.
 */
public class Block {
    private ArrayList<Line> blockLines;

    // Constructors
    public Block() {
    }

    public Block(ArrayList<Line> blockLines) {
        this.blockLines = blockLines;
    }

    // Getter

    public ArrayList<Line> getBlockLines() {
        return blockLines;
    }
}
