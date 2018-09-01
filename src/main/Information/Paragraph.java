package main.Information;
import org.jetbrains.annotations.NotNull;

import java.lang.Math;
import java.util.*;

import static java.lang.Double.min;
import static java.lang.Double.max;

/**
 * Created by Dang Tien Son on 7/18/2017.
 */
public class Paragraph implements Comparable{
    public Set<String> familyName = new TreeSet<>(Arrays.asList("phùng","đào","nguyễn","võ","ngô","phan","vũ","cấn","đỗ","đặng","phạm","trần","ths.","ths"));
    public Set<String> keyDict = new TreeSet<>(Arrays.asList("abstract","tómtắt", "keywords","keyword", "từkhóa","từkhóa:","từ khóa","abstract:","tómtắt:","tóm tắt:","keyword:","keywords:"));
    public int lineStart = 0, lineEnd = 0;
    public String type="none";
    public  double x1,y1,x2,y2;
    public ArrayList<Line> paragraphLines = new ArrayList<>();
    public Paragraph(){
    }
    public Paragraph(Line a) {
        paragraphLines.add(a);
        lineEnd = a.lineNumber;
        lineStart = a.lineNumber;
        x1 = a.getX1();
        x2 = a.getX2();
        y1 = a.getY1();
        y2 = a.getY2();

    }

    public void sortLine(){
        Collections.sort(paragraphLines);
    }
    public String toString() {
        String res = " ";
        for (Line l : paragraphLines) {
            res = res + l.toString()+"\n";
        }
        return res + "\nline start:" + lineStart + " line end: "+lineEnd+" x1: "+x1+" x2: "+x2+" y1: "+y1+" y2: "+y2+"\n";
    }
    public String content(){
        String res = " ";
        for (Line l : paragraphLines) {
            res = res + l.toString()+" ";
        }
        return res;
    }
    double verticalOverlapRatio(Paragraph p) {

        if (y2 <= p.y1 || p.y2 <= y1) return 0.0;
        double overlap = Math.min(y2, p.y2) - Math.max(y1, p.y1);
        return Math.max(overlap / (y2 - y1), overlap / (p.y2 - p.y1));
    }
    double widthOverlapRatio(Paragraph p) {
        if (x2 <= p.x1 || p.x2 <= x1) return 0.0;
        double overlap = Math.min(x2, p.x2) - Math.max(x1, p.x1);
        return Math.max(overlap / (x2 - x1), overlap / (p.x2 - p.x1));
    }
    public boolean isJoinableVertical(Paragraph p) {
        double overlapVariable = 0.5;
        return verticalOverlapRatio(p) >= overlapVariable;
    }
    public double averageHeight() {
        double res = 0.0;
        for (Line l : paragraphLines) {
            res += l.averageHeight();
        }
        return res/paragraphLines.size();
    }
    public double distanceRatio(Paragraph p){
        if (y1<= p.y1 && p.y2<=y2) return 0;
        if (p.y1<= y1 && y2<=p.y2) return 0;
        if (y1>p.y1)
            return (y1-p.y2)/ (p.y2-p.y1);
        else
            return (p.y1-y2)/ (y2-y1);
    }
    public boolean isJoinableWidth(Paragraph p) {
        double overlapVariable = 0.3, heightVariable = 2.0, distanceVariable = 4;
        //if (firstLine().isInLib(familyName)) return true;
        if ( widthOverlapRatio(p) <= overlapVariable ) return false;
        if ( distanceRatio(p) >= heightVariable ) return false;
        return true;
    }

    public void join(Paragraph p) {
        for (Line l : p.paragraphLines) {
            this.paragraphLines.add(l);
        }
        lineStart = Math.min(lineStart,p.lineStart);
        lineEnd = Math.max(lineEnd,p.lineEnd);
        x1 = min(x1,p.x1);
        x2 = max(x2,p.x2);
        y1 = min(y1,p.y1);
        y2 = max(y2,p.y2);
    }
    public double maxHeight() {
        double res = 0.0;
        for (Line l : paragraphLines) {
            res = Math.max(res, l.maxHeight());
        }
        return res;
    }
    public  void resetLineIndex(){
        int maxIndex=0,minIndex = 100000;
        y1=100000;
        x1=100000;
        y2=0;
        x2=0;
        for(Line l: paragraphLines){
            if (minIndex>l.lineNumber) minIndex = l.lineNumber;
            if (maxIndex<l.lineNumber) maxIndex = l.lineNumber;
            if (y1>l.getY1()) y1 = l.getY1();
            if (y2<l.getY2()) y2 = l.getY2();
            if (x1>l.getX1()) x1 = l.getX1();
            if (x2<l.getX2()) x2 = l.getX2();
        }
        lineEnd = maxIndex;
        lineStart = minIndex;

    }
    public double getFontSize(){
        double res = 0.0;
        for (Line w : paragraphLines) {
            res = Math.max(res, w.getFontSize());
        }
        return res;
    }
    @Override
    public int compareTo(@NotNull Object o) {
        if (o instanceof Paragraph){
            if (y1>((Paragraph) o).y1) return 1;
            else if (y1==((Paragraph) o).y1) return 0;
            else return -1;
        }
        return 0;
    }
}
