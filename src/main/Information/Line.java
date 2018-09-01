package main.Information;

import main.Demo.StyleExtractor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by nghia on 7/10/2017.
 */
public class Line implements Comparable{
    private ArrayList<Word> lineWords = new ArrayList<>();
    private double averageDistance = 0.0, x1, x2, y1, y2;
    public int lineNumber;
    public int pageNumber;


    // Constructors
    public Line() {
    }

    public Line(ArrayList<Word> lineWords) {
        this.lineWords = lineWords;
    }

    // Getter
    public ArrayList<Word> getLineWords() {
        return lineWords;
    }

    // Other functions
    public void setAverageDistance(double averageDistance) {
        this.averageDistance = averageDistance;
    }

    public void setPosition(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public double getAverageDistance() {
        return averageDistance;
    }

    public double AverageDistance() {
        ArrayList<Word> lineWords = getLineWords();
        double distance = 0.0;
        for (int i = 1; i < lineWords.size(); i++)
            distance = distance + lineWords.get(i).getPositionX() - lineWords.get(i - 1).getPositionX() - lineWords.get(i - 1).getWidth();
        return distance / (lineWords.size() - 1);
    }

    public double maxHeight() {
        double res = 0.0;
        for (Word w : lineWords) {
            res = Math.max(res, w.getHeight());
        }
        return res;
    }
    public double averageHeight() {
        double res = 0.0;
        for (Word w : lineWords) {
            res += w.getHeight();
        }
        return res/lineWords.size();
    }
    public double lineDistance(Line a) {
        return Math.abs(this.y1 - a.y1 - this.maxHeight());
    }



    public double getX1() {
        return x1;
    }

    public double getX2() {
        return x2;
    }

    public double getY1() {
        return y1;
    }

    public double getY2() {
        return y2;
    }
    boolean isIn(String a,String b){
        int n = a.length();
        int m = b.length();
        for(int i=0;i<m-n+1 && i<=3 ;i++){
            if (a.equals(b.substring(i,i+n)))
                return true;

        }
        return false;
    }
    public boolean isInLib(Set<String> l,int x){
        ArrayList<String> lineWords = new ArrayList<>();
        ArrayList<String> lineWords1 = new ArrayList<>();
        for(int i=0;i<this.lineWords.size();i++) {
            lineWords.add(this.lineWords.get(i).getWord().toLowerCase().replaceAll("\\P{L}+", ""));
            lineWords1.add(this.lineWords.get(i).getWord().toLowerCase().replaceAll("[^A-Za-z0-9]+", ""));
        }
        if (x!=0){
            String s1 = ((Integer)x).toString();
            String s2 = lineWords1.get(0);
            return s1.equals(s2);
        }
        String s="",s1="";
        for(int i=0;i<lineWords.size();i++) {
            s = s + lineWords.get(i);
            s1 = s1 + lineWords1.get(i);
        }
        //System.out.println("xxx "+s);
        for(String ss1 : l){
            if (isIn(ss1,s))
                return true;
        }
        for(String ss1 : l){
            if (isIn(ss1,s1))
                return true;
        }
        return false;
    }
    public double getFontSize(){
        double res = 0.0;
        for (Word w : lineWords) {
            res = Math.max(res, w.getFontSize());
        }
        return res;
    }
    public String toString() {
        StringBuilder res = new StringBuilder("");
        for (Word w : lineWords) {
            //res.append(w).append(" ").append(w.getHeight()).append(" ");
            res.append(w).append(" ");
        }
        return res.toString();
    }


    @Override
    public int compareTo(@NotNull Object o) {
        if (o instanceof Line){
            if (y1>((Line) o).getY1()) return 1;
            else return -1;
        }
        return 0;
    }

}
