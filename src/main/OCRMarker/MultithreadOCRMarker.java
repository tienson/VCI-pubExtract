package main.OCRMarker;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class MultithreadOCRMarker implements Runnable {
    private File pdfFile;

    public MultithreadOCRMarker(File pdfFile) {
        this.pdfFile = pdfFile;
    }

    @Override
    public void run() {
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(PDDocument.load(pdfFile));
            if (text.length() < 1000) {
                String pdfPath = pdfFile.getPath();
                File renamed = new File(pdfPath.substring(0, pdfPath.lastIndexOf('.')) + "OCR.pdf");
                pdfFile.renameTo(renamed);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
