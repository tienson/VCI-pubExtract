package main.Demo;

import com.google.common.base.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;
import main.Information.*;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.xml.sax.*;
import org.w3c.dom.*;
import org.apache.commons.io.FilenameUtils;

/**
 * Created by nghia on 6/24/2017.
 */

public class Test {
    StyleExtractor extractor;
    String content;
    ArrayList<Page> allPages;
    ArrayList<Paragraph> labeled;
    ArrayList<Sequence> sequences;
    PDDocument d;
    MyDocument myDocument;
    HashMap<Integer, Line> mapLine = new HashMap<>();
    public Set<String> keyDict = new TreeSet<>(Arrays.asList("conclusion", "summary", "abstract","abstracts", "tómtắt", "keywords", "keyword", "từkhóa","abstracts:", "từkhóa:", "từkhoá:", "từkhoá", "abstract:", "tómtắt:", "keyword:", "keywords:", "introduction", "đặtvấnđề", "imởđầu", "từkhúa", "mởđầu", "chứngminh", "abbreviations", "giớithiệu", "classiicationnumbers", "classiﬁcationnumbers", "ngàynhậnbài"));
    public Set<String> abs = new TreeSet<>(Arrays.asList("conclusion", "summary", "abstract","abstracts", "tómtắt", "abstract:", "tómtắt:","abstracts:", "summary:"));
    public Set<String> keyword = new TreeSet<>(Arrays.asList("keywords", "keyword", "từkhóa", "từkhóa:", "keyword:", "keywords:", "từkhoá:", "từkhoá", "từkhúa"));
    public Set<String> ref = new TreeSet<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));
    public Set<String> time = new TreeSet<>(Arrays.asList("ngày", "ngàytòa"));
    public Set<String> refHeader = new TreeSet<>(Arrays.asList("tàiliệuthamkhảo", "references", "references:", "reference:"));
    public Set<String> keyword1 = new TreeSet<>(Arrays.asList("keywords", "keyword", "từkhóa", "từkhóa:", "keyword:", "keywords:", "từkhoá:", "từkhoá", "từkhúa", "khóa", "khúa", "khoá", "word", "words"));
    public Set<String> abs1 = new TreeSet<>(Arrays.asList("conclusion", "summary", "abstract","abstracts","Abstracts", "tómtắt", "abstract:","abstracts:", "tómtắt:", "summary:", "tắt", "tắt:"));
    public Set<String> familyName = new TreeSet<>(Arrays.asList("tạ","ta","chử","chu","hứa","hua","duong","dương","thái","thai","giang","mai","trương","truong","nguyen","bui","hoang","dao","vo","ngo","vu","can","do","dang","pham","tran","le","bùi","hoàng","phùng","đào","nguyễn","võ","ngô","phan","vũ","cấn","đỗ","đặng","phạm","trần","ths.","ths","lê","ts","ts.","ks.","ks"));

    public Test() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        String fileName, filePath;
        File folder = new File("pdf");
        File[] listOfFiles = folder.listFiles();
        Test test = new Test();
        int count = 0;
        for (File file : listOfFiles) {
            if (file.getName().equals("5932.pdf"))
            {

                test.myDocument = new MyDocument();
                test.labeled = new ArrayList<>();
                fileName = file.getName();
                filePath = file.getPath();
                fileName = FilenameUtils.removeExtension(fileName);
                System.out.println(fileName);
                test.d = PDDocument.load(new File(filePath));
                test.extractor = new StyleExtractor(test.d);
                test.content = test.extractor.getContent();
                //System.out.println(test.content);
                test.setLineFeature();
                test.splitSequence();
                System.out.println(test.mapLine.get(test.nameSeq()));
                if (test.isFonterror()==true)
                    count++;

                //test.printRaw();
                //test.getMail();
                //test.getPhoneNumber();
                test.lineToParagraph();
                test.joinParagraphByCoordinate();

                test.splitParagraphByDictionary();
                test.joinParagraphByDictionary();
                //test.printParagraph();

                test.splitTitle1();
                test.splitAbtractKeyword();
                test.splitRef();

                test.resetParagraphLineIndex();
                test.addParagraph();
                test.sortLineParagraph(test.myDocument);
                test.saveToXML(fileName);
                test.saveLabeledBlockToXml(fileName);
                //test.saveMailPhoneNameToXml(fileName);test.printParagraph();
                test.printLabel();
            }
            //System.out.println("count= " +count);

        }

    }
    int ii = 0;
    boolean containFamilyName(Sequence seq){
        String s = seq.words.get(0).getWord(),s1 = seq.words.get(seq.words.size()-1).getWord();
        //System.out.println(s+" "+s1+"\n");
        s = s.toLowerCase().replaceAll("\\P{L}+", "");
        s1 = s1.toLowerCase().replaceAll("\\P{L}+", "");
        //System.out.println(s+" "+s1+"\n");
        if (familyName.contains(s) || familyName.contains(s1))
            return true;
        return false;

    }
    int nameSeq(){
        if (sequences.size()==0) return 3;
        for(Sequence seq: sequences) {
            if (seq.lineNumber == 1)
                continue;
            if (seq.words.size() >= 2 && seq.words.size() <= 5 && containFamilyName(seq))
                return seq.lineNumber;
        }
        return sequences.get(sequences.size()-1).lineNumber;
    }

    public ArrayList<String> getMail() {
        ArrayList<String> res = new ArrayList<>();
        Page page = allPages.get(0);
        ArrayList<Line> allLines = page.getPageLines();
        for (Line line : allLines) {
            if (StyleExtractor.checkLine(line.getLineWords()) == true) {
                if (line.isInLib(keyDict, 0) == true)
                    break;
                for (Word w:line.getLineWords()){
                    String s = w.getWord().replace(":"," ");
                    s = s.replace("-","");
                    s = s.replace(";","");
                    int last =0;
                    for(int i=0;i<s.length();i++){
                        if (s.charAt(i)==' ' || i==s.length()-1) {
                            if (s.substring(last,i).contains("@") && s.substring(last,i).contains("."))
                                res.add(s.substring(last,i+1));
                            last = i+1;
                        }

                    }
                }

            }
        }
//        for(String x :res){
//            System.out.println(x+"\n");
//        }
        return res;
    }
    public ArrayList<String> getPhoneNumber() {
        ArrayList<String> res = new ArrayList<>();
        Page page = allPages.get(0);
        ArrayList<Line> allLines = page.getPageLines();
        for (Line line : allLines) {
            if (StyleExtractor.checkLine(line.getLineWords()) == true) {
                if (line.isInLib(keyDict, 0) == true)
                    break;
                for (Word w:line.getLineWords()){
                    String s = w.getWord().replace(":"," ");
                    s = s.replace("-","");
                    s = s.replace(";","");
                    int last =0;
                    for(int i=0;i<s.length();i++){
                        if (s.charAt(i)==' ' || i==s.length()-1) {
                            if (i-last>=6 && s.substring(last,i).matches("[0-9]+") )
                                res.add(s.substring(last,i+1));
                            last = i+1;
                        }

                    }
                }
            }
        }
//        for(String x :res){
//            System.out.println(x+"\n");
//        }
        return res;
    }

    void lineToParagraph() {
        Page page = allPages.get(0);
        ArrayList<Line> allLines = page.getPageLines();
        for (Line line : allLines) {

            if (StyleExtractor.checkLine(line.getLineWords()) == true) {
                if (line.isInLib(keyDict, 0) == true) {
                    ii = line.lineNumber;
                    break;
                }
                myDocument.addParagraph(new Paragraph(line));
            }
        }
    }
    void splitSequence(){
        sequences = new ArrayList<>();
        double dis,lastPos,startPos,preSpace;
        ArrayList<Word> words = new ArrayList<>();
        int start,end;
        Word lastWord;
        Page page = allPages.get(0);
        ArrayList<Line> allLines = page.getPageLines();
        for (Line line : allLines) {

            if (StyleExtractor.checkLine(line.getLineWords()) == true) {
                if (line.isInLib(keyDict, 0) == true)
                    break;

                start = 0;
                end =-1;
                dis=0;
                words = new ArrayList<>();
                startPos = line.getX1();
                lastPos = line.getX2();
                for(Word w:line.getLineWords()) {
                    if (end !=0 && end-start+1>=2) {
                        //System.out.println(w + " " + w.getPositionX() + " " + w.getWidth() + "\n");
                        lastPos = line.getLineWords().get(end).getWidth() + line.getLineWords().get(end).getPositionX();
                        startPos = line.getLineWords().get(start).getWidth();
                        lastWord = line.getLineWords().get(end);
                        preSpace = line.getLineWords().get(end).getPositionX() - line.getLineWords().get(end - 1).getPositionX() - line.getLineWords().get(end - 1).getWidth();

                        //System.out.println(line.lineNumber + " " + line.getLineWords().get(end) + " " + preSpace + " " + (w.getPositionX() - lastPos) + "\n");
                        if ((start != end && (w.getPositionX() - lastPos) >= 2 * preSpace)
                                || (lastWord.getWord().charAt(lastWord.getWord().length()-1) == ',')
                                || (lastWord.getWord().charAt(lastWord.getWord().length()-1) == ';')
                                || (lastWord.getWord().charAt(lastWord.getWord().length()-1) == '.')
                                || (lastWord.getWord().charAt(lastWord.getWord().length()-1) == '-')
                                || (lastWord.getWord().charAt(lastWord.getWord().length()-1) == '–')
                                || (lastWord.getWord().equals("AND"))) {
                            sequences.add(new Sequence(line.lineNumber, startPos, lastPos, words));
                            start = line.getLineWords().indexOf(w);
                            end = start-1;
                            dis = 0;
                            words = new ArrayList<>();

                        }
                    }
                    dis = dis + w.getWidth();
                    words.add(w);
                    end++;
                }
                sequences.add(new Sequence(line.lineNumber, startPos, line.getX2(), words));
            }
        }
//        for(Sequence seq: sequences){
//            for(Word w: seq.words)
//                System.out.println(w+" ");
//            System.out.println("\n");
//        }
    }
    void splitAbtractKeyword() {
        ArrayList<Line> allLine = new ArrayList<>();
        Page page;
        for (int j = 0; j < allPages.size(); j++) {
            for (int i = 0; i < allPages.get(j).getPageLines().size(); i++) {
                allLine.add(allPages.get(j).getPageLines().get(i));
                allPages.get(j).getPageLines().get(i).pageNumber = j;
            }
        }
        Line line;
        double x1, x2;
        double lastY2 = 0;
        int lastIndex = 0;
        myDocument.addParagraph(new Paragraph(allLine.get(0)));

        for (int i = 1; i < allLine.size(); i++) {
            line = allLine.get(i);
            mapLine.put(line.lineNumber, line);
            if (StyleExtractor.checkLine(line.getLineWords()) == true) {
                lastIndex = i;
                //System.out.println("xxx"+line+" "+lastIndex);
                if (line.isInLib(keyDict, 0) || line.getY2() < lastY2) {
                    Paragraph p = new Paragraph(line);
                    myDocument.addParagraph(p);
                    lastY2 = line.getY2();
                    if (line.isInLib(abs, 0)) {
                        p.type = "Abstract";
                        labeled.add(p);
                    }
                    if (line.isInLib(keyword, 0)) {
                        p.type = "Keyword";
                        labeled.add(p);
                    }
                } else if (lastIndex != 0) {
                    Paragraph p1 = myDocument.getDocumentParagraphs().get(myDocument.getDocumentParagraphs().size() - 1);
                    Line l = p1.paragraphLines.get(p1.paragraphLines.size() - 1);
                    Line l1;
                    x1 = allLine.get(lastIndex).getY2() - allLine.get(lastIndex - 1).getY2();
                    if (p1.paragraphLines.size() >= 2) {
                        l1 = p1.paragraphLines.get(p1.paragraphLines.size() - 2);
                        x1 = l.getY2() - l1.getY2();
                    }
                    x2 = line.getY2() - l.getY2();
                    //System.out.println("-->" + line.toString() + "\n" + l + " " + line.pageNumber + " " + l.pageNumber);

                    if (x1 == 0 || ((x2 / x1) <= 1.3) ||
                            (p1.type.equals("Keyword") == false && p1.paragraphLines.size() == 1)
                            || (p1.type.equals("Abstract") == true && line.pageNumber != l.pageNumber)) {

                        String lastChar = "";
                        Word w = l.getLineWords().get(l.getLineWords().size() - 1);
                        lastChar = w.getWord().substring(w.getWord().length() - 1, w.getWord().length());
                        if (line.getY2() < l.getY2()) continue;
                        if (p1.type.equals("Keyword") == true && (lastChar.equals(".") || line.isInLib(ref, 2) == true || line.getLineWords().get(0).isBold()))
                            myDocument.addParagraph(new Paragraph(line));
                        myDocument.getDocumentParagraphs().get(myDocument.getDocumentParagraphs().size() - 1).paragraphLines.add(line);

                    } else
                        myDocument.addParagraph(new Paragraph(line));
                } else
                    myDocument.addParagraph(new Paragraph(line));

            }
        }


    }
    int align(Line l){
        double d1 = l.getX1();
        double d2 = extractor.getAllPages().get(0).getWidth()-l.getX2();
        //System.out.println("d1 d2 "+d1+" "+ d2);
        if (d1<=0.7*d2) return 1;
        else if (d1>=d2*1.7) return 3;
        else return 2;
    }
    List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();
    LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard()).withProfiles(languageProfiles).build();
    TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
    void splitTitle() throws IOException {
        int x = nameSeq();

        String myText = mapLine.get(x).toString();
        TextObject textObject = textObjectFactory.forText(myText);
        com.google.common.base.Optional<LdLocale> lang = languageDetector.detect(textObject);
        //System.out.println(myText);
        if (!lang.isPresent()) {
            splitTitle3();
            return;
        }

        //System.out.println(lang.get().getLanguage());

        if (lang.get().getLanguage().equals("vi"))
            splitTitle2();
        else
            splitTitle3();
    }

    void splitTitle2(){

        int x = nameSeq();
        //System.out.println("DO 2");
        if (x<2) return;
        Paragraph newp = new Paragraph();
        newp.type = "Title";
        int x1=x-1,x2,x3;

        while (StyleExtractor.checkLine(mapLine.get(x1).getLineWords()) == false)
            x1--;
        newp.paragraphLines.add(mapLine.get(x1));

        double dis1,dis2;
        int t=1;
        for(int i=x1-1;i>0;i--)
            if (StyleExtractor.checkLine(mapLine.get(i).getLineWords()) == true) {
                x2 = i + 1;
                while (StyleExtractor.checkLine(mapLine.get(x2).getLineWords()) == false)
                    x2++;
                x3 = x2+1;
                while (StyleExtractor.checkLine(mapLine.get(x3).getLineWords()) == false)
                    x3++;
                dis1 = mapLine.get(x2).getY1() - mapLine.get(i).getY2();
                dis2 = mapLine.get(x3).getY1() - mapLine.get(x2).getY2();
                if (t == 1) {
                    dis2 = dis1;
                    t++;
                }
                //System.out.println(mapLine.get(i)+" "+dis1+" "+dis2+" "+align(mapLine.get(i))+" "+align(mapLine.get(i+1)));
                if (align(mapLine.get(i)) == align(mapLine.get(x1)) && 0.8 * dis2 <= dis1 && dis1 <= 1.2 * dis2
                        //&& mapLine.get(i).getLineWords().get(0).isBold()== mapLine.get(i).getLineWords().get(0).isBold()
                       // && mapLine.get(i).getLineWords().get(0).isItalic()== mapLine.get(i).getLineWords().get(0).isItalic()
                    )
                    newp.paragraphLines.add(mapLine.get(i));
                else break;
            }
        newp.resetLineIndex();
        newp.sortLine();
        if (newp.paragraphLines.size() != 0) {
            labeled.add(newp);
            myDocument.addParagraph(newp);
        }
    }
    void splitTitle1() {
        //System.out.println("DO 1");
        if (isFonterror()==false) {
            Paragraph p;
            Paragraph newp1 = new Paragraph();
            newp1.type = "Title";
            Paragraph newp = new Paragraph();
            newp.type = "Author";
            Line l;
            int i1 = 0, i;
            boolean d = false;
            double maxFontSize = 0.0, maxFontSize1=0.0;
            for (Paragraph p1 : myDocument.getDocumentParagraphs()) {
                if (maxFontSize< p1.getFontSize()){
                    maxFontSize1 = maxFontSize;
                    maxFontSize = p1.getFontSize();
                }
                else
                    maxFontSize1 = Math.max(maxFontSize1,p1.getFontSize());
            }
            int dem = 0;
            for (i = ii - 1; i >= 0; i--)
                if (mapLine.get(i) != null && StyleExtractor.checkLine(mapLine.get(i).getLineWords()))
                if (maxFontSize == mapLine.get(i).getFontSize())
                    dem = mapLine.get(i).getLineWords().size();
            if (dem<=3)
                maxFontSize = maxFontSize1;
            for (i = ii - 1; i >= 0; i--)
                if (mapLine.get(i) != null && StyleExtractor.checkLine(mapLine.get(i).getLineWords())) {
                    l = mapLine.get(i);
                    if (maxFontSize == l.getFontSize()  && (newp1.paragraphLines.size()==0 || Math.abs(l.getY1()- mapLine.get(i+1).getY1())<=100)) {
                        newp1.paragraphLines.add(l);
                        if (!d)
                            i1 = i + 1;
                        d = true;
                    } else if (d) break;
                }
            for (int j = i1; j < ii; j++) {
                if (mapLine.get(j) != null && StyleExtractor.checkLine(mapLine.get(j).getLineWords()) && mapLine.get(j).getFontSize() != maxFontSize && !mapLine.get(j).isInLib(time, 0))
                    newp.paragraphLines.add(mapLine.get(j));
            }
            newp1.resetLineIndex();
            newp.resetLineIndex();
            newp.sortLine();
            newp1.sortLine();
            if (newp.paragraphLines.size() != 0) {
                labeled.add(newp);
                myDocument.addParagraph(newp);
            }
            if (newp1.paragraphLines.size() != 0) {
                labeled.add(newp1);
                myDocument.addParagraph(newp1);
            }

        }
        else{
            double maxHeight= 0.0;
            for (Paragraph p1 : myDocument.getDocumentParagraphs()) {
                if (p1.y1<400)
                    maxHeight = Math.max(maxHeight, p1.averageHeight());
            }
            for (Paragraph p1 : myDocument.getDocumentParagraphs()) {
                if (maxHeight == p1.averageHeight() && p1.y1<400){
                    if (p1.paragraphLines.size()==0)
                        continue;
                    if (p1.paragraphLines.get(0).getLineWords().size()<=2)
                        p1.paragraphLines.remove(0);
                    if (p1.paragraphLines.size()==0)
                        continue;
                    p1.type = "Title";
                    labeled.add(p1);
                    break;
                }
            }
        }
    }
    void splitTitle3(){
        Paragraph p = new Paragraph();
        Paragraph res =p;
        Line l1,l2;
        double dis1,dis2,max=0.0;
        Page page = allPages.get(0);
        ArrayList<Line> allLines = page.getPageLines();
        for (Line line : allLines) {
            if (StyleExtractor.checkLine(line.getLineWords()) == true) {
                if (line.isInLib(keyDict, 0) == true)
                    break;
                if (p.paragraphLines.size()==0) {
                    p.paragraphLines.add(line);
                    continue;
                }
                l2 = p.paragraphLines.get(p.paragraphLines.size()-1);
                if (p.paragraphLines.size()==1)
                    if (//align(line) == align(l2) &&
                             l2.getLineWords().get(0).isBold() == line.getLineWords().get(0).isBold()
                            && l2.getLineWords().get(0).isItalic() == line.getLineWords().get(0).isItalic()
                            && l2.getLineWords().get(0).getFontSize() == line.getLineWords().get(0).getFontSize()) {
                        p.paragraphLines.add(line);
                        continue;
                    } else {
                        if (max<p.averageHeight()){
                            max = p.averageHeight();
                            res=p;
                        }
                        p = new Paragraph();
                        p.paragraphLines.add(line);
                        continue;
                    }

                l1 = p.paragraphLines.get(p.paragraphLines.size()-2);
                dis2  = line.getY1() - l2.getY2();
                dis1 = l2.getY1() - l1.getY2();
                //System.out.println(line+ " " + align(line)+ " "+align(l2));
                if (//align(line) == align(l2) &&
                        0.8 * dis2 <= dis1 && dis1 <= 1.2 * dis2)
                    p.paragraphLines.add(line);
                else{
                    if (max<p.averageHeight()){
                        max = p.averageHeight();
                        res=p;
                    }
                    p = new Paragraph();
                    p.paragraphLines.add(line);

                    continue;
                }

            }

        }
        p = res;
        p.type = "Title";
        p.resetLineIndex();
        p.sortLine();

        if (p.paragraphLines.size() != 0) {
            labeled.add(p);
            myDocument.addParagraph(p);
        }
    }
    void splitRef() {
        myDocument.addParagraph(new Paragraph());
        int i = 2, index, lastIndex = 0;
        double x1, x2, x21, x22;
        boolean d = false;
        boolean check = true;
        Page page;
        ArrayList<Line> allLines;
        if (allPages.size() >= 3) {
            page = allPages.get(allPages.size() - 2);
            allLines = page.getPageLines();
            for (Line line : allLines) {
                mapLine.put(line.lineNumber, line);
                if (line.isInLib(refHeader, 0)) check = true;
                if (check == false) continue;
                if (StyleExtractor.checkLine(line.getLineWords()) == true) {
                    if (line.isInLib(ref, 1)) {
                        d = true;
                        i = 2;
                        Paragraph p = new Paragraph(line);
                        myDocument.addParagraph(p);
                        lastIndex = allLines.indexOf(line);
                        p.type = "ref";
                        labeled.add(p);
                    } else if (line.isInLib(ref, i) && d == true) {
                        Paragraph p = new Paragraph(line);
                        myDocument.addParagraph(p);
                        lastIndex = allLines.indexOf(line);
                        p.type = "ref";
                        labeled.add(p);
                        i++;
                    } else if (lastIndex != 0) {
                        index = allLines.indexOf(line);
                        x1 = allLines.get(lastIndex).getY2() - allLines.get(lastIndex - 1).getY2();
                        x2 = allLines.get(index).getY2() - allLines.get(lastIndex).getY2();
                        x21 = allLines.get(lastIndex - 1).getX2();
                        x22 = allLines.get(lastIndex).getX2();
                        if ((x2 / x1) <= 1.5 && (myDocument.getDocumentParagraphs().get(myDocument.getDocumentParagraphs().size() - 1).paragraphLines.size() <= 1 ||
                                (myDocument.getDocumentParagraphs().get(myDocument.getDocumentParagraphs().size() - 1).paragraphLines.size() >= 2 && Math.abs(x21 - x22) <= 20))) {
                            myDocument.getDocumentParagraphs().get(myDocument.getDocumentParagraphs().size() - 1).paragraphLines.add(line);
                            lastIndex = allLines.indexOf(line);
                        }
                    }
                }
            }
        }
        lastIndex = 0;
        if (allPages.size() >= 2) {
            page = allPages.get(allPages.size() - 1);
            allLines = page.getPageLines();
            for (Line line : allLines) {
                mapLine.put(line.lineNumber, line);
                if (line.isInLib(refHeader, 0)) check = true;
                if (check == false) continue;
                if (StyleExtractor.checkLine(line.getLineWords()) == true) {
                    if (line.isInLib(ref, 1)) {
                        d = true;
                        i = 2;
                        Paragraph p = new Paragraph(line);
                        myDocument.addParagraph(p);
                        lastIndex = allLines.indexOf(line);
                        p.type = "ref";
                        labeled.add(p);
                    } else if (line.isInLib(ref, i) && d == true) {
                        Paragraph p = new Paragraph(line);
                        myDocument.addParagraph(p);
                        lastIndex = allLines.indexOf(line);
                        p.type = "ref";
                        labeled.add(p);
                        i++;
                    } else if (lastIndex != 0) {
                        index = allLines.indexOf(line);
                        x1 = allLines.get(lastIndex).getY2() - allLines.get(lastIndex - 1).getY2();
                        x2 = allLines.get(index).getY2() - allLines.get(lastIndex).getY2();
                        x21 = allLines.get(lastIndex - 1).getX2();
                        x22 = allLines.get(lastIndex).getX2();
                        if ((x2 / x1) <= 1.5 && (myDocument.getDocumentParagraphs().get(myDocument.getDocumentParagraphs().size() - 1).paragraphLines.size() <= 1 ||
                                (myDocument.getDocumentParagraphs().get(myDocument.getDocumentParagraphs().size() - 1).paragraphLines.size() >= 2 && Math.abs(x21 - x22) <= 20))) {
                            myDocument.getDocumentParagraphs().get(myDocument.getDocumentParagraphs().size() - 1).paragraphLines.add(line);
                            lastIndex = allLines.indexOf(line);
                        }
                        //else myDocument.addParagraph(new Paragraph(line));
                    }
                }
            }
        }
    }

    void resetParagraphLineIndex() {
        for (Paragraph p : myDocument.getDocumentParagraphs()) {
            p.resetLineIndex();
        }
    }

    public void printRaw() throws IOException {

        //System.out.println("Content: " + content);
        System.out.println("----------------------------------------------------------------------------------------------\n");
        allPages = extractor.getAllPages();

        for (Page page : allPages) {
            System.out.println("Page " + page.getPageNumber() + ":");
            ArrayList<Line> allLines = page.getPageLines();
            double preDistance = 1.0;
            for (Line line : allLines) {
                System.out.print("Line" + line.lineNumber + ": ");
                //System.out.print("(" + line.getX1() + "," + line.getX2() + ") ");
                System.out.println(line.toString()+" "+line.getFontSize());
                //System.out.println(" (" + line.getY1() + "," + line.getY2() + ") " + line.getFontSize() +" " + line.averageHeight());
                for(Word w:line.getLineWords())
                    System.out.print(w.getHeight()+" ");
                System.out.println("\n");
            }
            System.out.println("");
        }
        System.out.println("----------------------------------------------------------------------------------------------\n");
    }
    boolean isFonterror() {
        allPages = extractor.getAllPages();
        int numberOfLine =0;
        int count =0;
        for (Page page : allPages) {
            ArrayList<Line> allLines = page.getPageLines();
            numberOfLine+=allLines.size();

            for (Line line : allLines)
                if (line.getLineWords().size() > 0) {
                    float wordHeight = line.getLineWords().get(0).getHeight();
                    boolean d= true;
                    for (Word w : line.getLineWords()) {
                        if (w.getHeight()!=wordHeight)
                            d=false;
                    }
                    if (d)
                        count++;
                }
        }
        //System.out.println("aasdas "+count+" "+numberOfLine+" "+count/numberOfLine);
        if (count>0.7*numberOfLine) return true;
        else return false;
    }
    void setLineFeature() {
        allPages = extractor.getAllPages();
        int i = 1;
        double pageHeight = 0;
        for (Page page : allPages) {
            ArrayList<Line> allLines = page.getPageLines();
            double preDistance = 1.0;
            for (Line line : allLines) {

                    line.lineNumber = i;
                    mapLine.put(line.lineNumber, line);
                    i++;
                    ArrayList<Word> lineWord = line.getLineWords();
                    if (Double.isNaN(line.AverageDistance()) || line.AverageDistance() <= 0)
                        line.setAverageDistance(preDistance);
                    else line.setAverageDistance(line.AverageDistance());
                    preDistance = line.getAverageDistance();
                    Word firstWord = lineWord.get(0);
                    Word lastWord = lineWord.get(lineWord.size() - 1);
                    line.setPosition(firstWord.getPositionX(), firstWord.getPositionY() - line.maxHeight() + pageHeight, lastWord.getPositionX() + lastWord.getWidth(), lastWord.getPositionY() + pageHeight);


            }
            pageHeight += page.getHeight();
        }

    }

    void joinParagraphByCoordinate() {
        joinByLine();
        joinByWidth();
        joinByVertical();
        sortLineParagraph(myDocument);
    }

    void joinByLine() {
        Paragraph p1, p2;
        int s = myDocument.getDocumentParagraphs().size();
        for (int i = s - 1; i >= 0; i--)
            for (int j = i - 1; j >= 0; j--) {
                p1 = myDocument.getDocumentParagraphs().get(i);
                p2 = myDocument.getDocumentParagraphs().get(j);
                if (p1.y2 == p2.y2) {
                    p2.join(p1);
                    myDocument.getDocumentParagraphs().remove(i);
                    break;
                }
            }
    }

    void joinByWidth() {
        Paragraph p1, p2;
        int s = myDocument.getDocumentParagraphs().size();
        DisjoinSet dis = new DisjoinSet(s + 1);

        for (int i = 0; i < s; i++)
            for (int j = i + 1; j < s; j++) {
                p1 = myDocument.getDocumentParagraphs().get(i);
                p2 = myDocument.getDocumentParagraphs().get(j);
                if (p1.isJoinableWidth(p2)) {
                    dis.join(j, i);
                    //System.out.println("Joinable:\n" + i + "\n" + j);
                }
            }
        //for(int i=0;i<s;i++){
        //    System.out.println(i+"\n"+dis.ans(i));
        //}
        for (int i = 0; i < s; i++) {
            p2 = myDocument.getDocumentParagraphs().get(i);
            p1 = myDocument.getDocumentParagraphs().get(dis.ans(i));
            if (dis.par[i] != i) {
                p1.join(p2);
            }
        }
        for (int i = s - 1; i >= 0; i--) {
            if (dis.par[i] != i)
                myDocument.getDocumentParagraphs().remove(i);
        }
    }

    void joinByVertical() {
        Paragraph p1, p2;
        int s = myDocument.getDocumentParagraphs().size();
        for (int i = s - 1; i >= 0; i--)
            for (int j = i - 1; j >= 0; j--) {
                p1 = myDocument.getDocumentParagraphs().get(i);
                p2 = myDocument.getDocumentParagraphs().get(j);
                if (p1.isJoinableVertical(p2)) {
                    p2.join(p1);
                    myDocument.getDocumentParagraphs().remove(i);
                    break;
                }
            }

    }
    void sortLineParagraph(MyDocument m) {
        resetParagraphLineIndex();
        for (Paragraph p : m.getDocumentParagraphs())
            p.sortLine();
        m.sortParagraph();
    }
    boolean isName(String s) {
        try {
            String URL = "http://112.137.131.9/vci-scholar/public/api/extraction/predictAuthor/" + s; //just a string
            String sURL = URL.replaceAll(" ", "%20");
            //System.out.println(sURL);
            // Connect to the URL using java's native library
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
            String predict = rootobj.get("predict").getAsString();
            //System.out.println(predict);
            if (predict.equals("true"))
                return true;
            else
                return false;
        }finally {
            return false;
        }
    }
    void splitParagraphByDictionary() throws IOException {
        Paragraph np,p;
        MyDocument newdoc = new MyDocument();
        for(int i= myDocument.getDocumentParagraphs().size()-1;i>=0;i--){
            p = myDocument.getDocumentParagraphs().get(i);
            np = new Paragraph();
            for(Line l : p.paragraphLines){
                //System.out.println(l.getLineWords().get(0)+" "+l.isInLib(familyName,0));
                String s = "";
                for(int j =0;j < l.getLineWords().size() && j<=3;j++)
                    s = s + l.getLineWords().get(j).getWord()+" ";
                s = s.substring(0,s.length()-1);
                //if (np.paragraphLines.size()!=0 && (isName(s) || l.isInLib(keyDict,0))){
                if (np.paragraphLines.size()!=0 && (l.isInLib(familyName,0) || l.isInLib(keyDict,0))){
                   newdoc.addParagraph(np);
                   np = new Paragraph();
                }
                np.paragraphLines.add(l);
            }
            if (np.paragraphLines.size()!=0)
                newdoc.addParagraph(np);
        }
        myDocument = newdoc;
        sortLineParagraph(myDocument);

    }

    void joinParagraphByDictionary() {
        boolean[] d = new boolean[10000];
        Paragraph p;
        for (int i = 0; i < myDocument.getDocumentParagraphs().size(); i++) {
            p = myDocument.getDocumentParagraphs().get(i);
            if (p.paragraphLines.size() == 1 && p.paragraphLines.get(0).isInLib(keyDict, 0) && i != myDocument.getDocumentParagraphs().size() - 1 && p.paragraphLines.get(0).getLineWords().size() <= 3) {
                p.join(myDocument.getDocumentParagraphs().get(i + 1));
                d[i + 1] = true;
            }
        }
        for (int i = myDocument.getDocumentParagraphs().size() - 1; i >= 0; i--) {
            if (d[i] == true)
                myDocument.getDocumentParagraphs().remove(i);
        }
        sortLineParagraph(myDocument);
    }

    void addParagraph() {
        ArrayList<Paragraph> newList = new ArrayList<>();
        Paragraph np;
        for (int i = 0; i < myDocument.getDocumentParagraphs().size(); i++) {
            Paragraph p = myDocument.getDocumentParagraphs().get(i);
            if (mapLine.get(p.lineEnd + 1) != null && StyleExtractor.checkLine(mapLine.get(p.lineEnd + 1).getLineWords()) == false) {
                np = new Paragraph(mapLine.get(p.lineEnd + 1));
                np.lineStart = p.lineStart;
                np.lineEnd = p.lineEnd + 1;
                newList.add(np);
            }
        }
        for (Paragraph p : newList)
            myDocument.addParagraph(p);
        sortLineParagraph(myDocument);
    }

    void printLabel() {
        for (Paragraph p : labeled) {

            String content = p.toString();
            String type = p.type;
            System.out.println(type + ":\n" + content);
        }
    }

    void printParagraph() {
        int i = 1;
        System.out.println(myDocument.getDocumentParagraphs().size());
        for (Paragraph p : myDocument.getDocumentParagraphs()) {
            System.out.println("Paragraph:" + i + "\n" + p.toString());
            i++;
        }
    }

    ArrayList<String> getKeywords(Paragraph p) {
        ArrayList<String> res = new ArrayList<>();
        if (p == null) return res;
        while (p.paragraphLines.get(0).isInLib(keyword1, 0))
            p.paragraphLines.get(0).getLineWords().remove(0);
        String s = p.content();
        int last = 0;
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if (c == ',' || c == '.' || c == ';') {
                res.add(s.substring(last, i));
                last = i + 1;
            }
        }
        return res;
    }

    public void saveLabeledBlockToXml(String fileName) {
        Document dom;
        Element xml, id, info = null, url = null, source = null, language, titles, title, authors, author,
                name, email, affiliation, tel, journal, volume, number, year, doi,
                abstracts, abstract_, all_keywords, keywords_raw, keywords, keyword,
                references, reference, ref, raw, type, publisher, place, day, month, meta, link, references_raw, is_reviewed;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.newDocument();
            xml = dom.createElement("xml");

            id = dom.createElement("id");
            xml.appendChild(id);

            info = dom.createElement("info");
            url = dom.createElement("url");
            source = dom.createElement("source");
            info.appendChild(url);
            info.appendChild(source);
            xml.appendChild(info);

            language = dom.createElement("language");
            xml.appendChild(language);

            for (Paragraph p : labeled) {
                title = dom.createElement("title");
                if (p.type.equals("Title")) {
                    title.appendChild(dom.createTextNode(String.valueOf(p.content())));
                    xml.appendChild(title);
                    break;
                }
            }

            authors = dom.createElement("authors");
            author = dom.createElement("author");
            email = dom.createElement("email");
            affiliation = dom.createElement("affiliation");
            tel = dom.createElement("tel");
            author.appendChild(email);
            author.appendChild(affiliation);
            author.appendChild(tel);
            authors.appendChild(author);
            xml.appendChild(authors);

            journal = dom.createElement("journal");
            xml.appendChild(journal);
            volume = dom.createElement("volume");
            xml.appendChild(volume);
            number = dom.createElement("number");
            xml.appendChild(number);
            year = dom.createElement("year");
            xml.appendChild(year);
            doi = dom.createElement("doi");
            xml.appendChild(doi);


            for (Paragraph p : labeled) {
                abstract_ = dom.createElement("abstract");
                if (p.type.equals("Abstract")) {
                    while (p.paragraphLines.get(0).isInLib(abs1, 0))
                        p.paragraphLines.get(0).getLineWords().remove(0);
                    abstract_.appendChild(dom.createTextNode(String.valueOf(p.content())));
                    xml.appendChild(abstract_);
                    break;
                }
            }

            Paragraph p1 = null;
            for (Paragraph p : labeled) {
                keywords_raw = dom.createElement("keywords_raw");
                if (p.type.equals("Keyword")) {
                    //System.out.println("keyword cac kieu:\n"+getKeywords(p));
                    keywords_raw.appendChild(dom.createTextNode(String.valueOf(p.content())));
                    xml.appendChild(keywords_raw);
                    p1 = p;
                    break;
                }
            }
            keywords = dom.createElement("keywords");
            ArrayList<String> k = getKeywords(p1);
            for (String s : k) {
                keyword = dom.createElement("keyword");
                keyword.appendChild(dom.createTextNode(s));
                keywords.appendChild(keyword);
            }
            xml.appendChild(keywords);

            // còn references và references_raw
            references = dom.createElement("references");
            for (Paragraph p : labeled) {
                if (p.type.equals("ref")) {
                    reference = dom.createElement("reference");
                    raw = dom.createElement("raw");
                    ref = dom.createElement("ref");
                    raw.appendChild(dom.createTextNode(p.content()));
                    ref.appendChild(raw);
                    reference.appendChild(ref);
                    references.appendChild(reference);
                }
            }
            xml.appendChild(references);

            is_reviewed = dom.createElement("is_reviewed");
            xml.appendChild(is_reviewed);

            dom.appendChild(xml);

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                File file = new File("C:\\Users\\Dang Tien Son\\Desktop\\xml output (labeled block)\\" + fileName + ".xml");
                tr.transform(new DOMSource(dom),
                        new StreamResult(new FileOutputStream(file)));

            } catch (TransformerException te) {
                System.out.println(te.getMessage());
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        } catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }

    }

    public void saveToXML(String fileName) {
        Document dom;
        Element lineStart = null, lineEnd = null, id = null;

        // instance of a DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use factory to get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // create instance of DOM
            dom = db.newDocument();

            // create the root element
            Element xmlTag = dom.createElement("xml");

            Element blocksTag = dom.createElement("blocks");
            int i = 0;
            for (Paragraph p : myDocument.getDocumentParagraphs())
            if (!p.type.equals("none"))
            {
                Element blockTag = dom.createElement("block");
                id = dom.createElement("id");
                id.appendChild(dom.createTextNode(String.valueOf(i++)));
                lineStart = dom.createElement("line-start");
                lineStart.appendChild(dom.createTextNode(String.valueOf(p.lineStart)));
                lineEnd = dom.createElement("line-end");
                lineEnd.appendChild(dom.createTextNode(String.valueOf(p.lineEnd)));
                blockTag.appendChild(id);
                blockTag.appendChild(lineStart);
                blockTag.appendChild(lineEnd);
                blocksTag.appendChild(blockTag);
            }
            xmlTag.appendChild(blocksTag);
            dom.appendChild(xmlTag);


            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                //tr.setOutputProperty(OutputKeys.METHOD, "xml");
                //tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                //tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                // send DOM to file
                File file = new File("C:\\Users\\Dang Tien Son\\Desktop\\xml output\\" + fileName + ".xml");
                tr.transform(new DOMSource(dom),
                        new StreamResult(new FileOutputStream(file)));

            } catch (TransformerException te) {
                System.out.println(te.getMessage());
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        } catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }
    }
    public void saveMailPhoneNameToXml(String fileName){
        Document dom;
        Element mails = null, phones = null, name = null,names = null,mail=null,phone=null;


        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.newDocument();
            Element xmlTag = dom.createElement("xml");
            phones = dom.createElement("phones");
            for(String m: getPhoneNumber()) {

                phone = dom.createElement("phone");
                phone.appendChild(dom.createTextNode(m));
                phones.appendChild(phone);
            }

            mails = dom.createElement("mails");
            for(String m: getMail()) {

                mail = dom.createElement("mail");
                mail.appendChild(dom.createTextNode(m));
                mails.appendChild(mail);
            }

            names = dom.createElement("names");
            for(Sequence seq: sequences) {
                if (seq.words.size() >= 2 && seq.words.size() <= 5 && containFamilyName(seq)) {
                    name = dom.createElement("name");
                    name.appendChild(dom.createTextNode(seq.toString()));
                    names.appendChild(name);
                }

            }
            xmlTag.appendChild(mails);
            xmlTag.appendChild(phones);
            xmlTag.appendChild(names);
            dom.appendChild(xmlTag);
            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                //tr.setOutputProperty(OutputKeys.METHOD, "xml");
                //tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                //tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                // send DOM to file
                File file = new File("C:\\Users\\Dang Tien Son\\Desktop\\phone_name_mail\\" + fileName + ".xml");
                tr.transform(new DOMSource(dom),
                        new StreamResult(new FileOutputStream(file)));

            } catch (TransformerException te) {
                System.out.println(te.getMessage());
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        } catch (ParserConfigurationException pce) {
        System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
    }
    }

}

