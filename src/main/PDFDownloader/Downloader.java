package main.PDFDownloader;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import javax.xml.transform.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Downloader {
    private boolean[] usedID = new boolean[100];
    private SAXBuilder[] saxBuilders;

    public static void main(String[] args) {
        try {
            Downloader downloader = new Downloader();
            downloader.downloadPDF(new File("D:\\Information Extraction\\pdf and xml\\data_trang_fixed\\VNU Journal of Science_ Legal Studies"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Downloader() {
        saxBuilders = new SAXBuilder[Runtime.getRuntime().availableProcessors()];

        for (int i = 0; i < saxBuilders.length; i++) {
            saxBuilders[i] = new SAXBuilder();
            // Ignore .dtd file
            saxBuilders[i].setFeature("http://xml.org/sax/features/validation", false);
            saxBuilders[i].setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            saxBuilders[i].setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        }
    }

    public void downloadPDF(File xmlFolder) throws JDOMException, IOException, TransformerConfigurationException {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (File file : xmlFolder.listFiles()) {
            if (file.getName().endsWith(".xml")) {
                executorService.execute(new ProcessXML(file));
            }
        }

        executorService.shutdown();

        for (int i = 0; i < 100; i++) {
            if (usedID[i]) {
                System.out.print(i + " ");
                if (i % 10 == 0) {
                    System.out.println("");
                }
            }
        }
    }

    public void downloadTo(URL url, String path) throws IOException {
        URLConnection connection = url.openConnection();
        String contentDisposition = connection.getHeaderField("Content-Disposition");

        if (contentDisposition != null && contentDisposition.contains("filename=\"")) {
            String originalName = contentDisposition.substring(contentDisposition.indexOf("filename=\"") + 10, contentDisposition.length() - 1);    // The last index is "
            String extension = originalName.substring(originalName.lastIndexOf('.'), originalName.length());
            path = path + extension;
            FileUtils.copyURLToFile(url, new File(path));
        }
    }

    public String getOriginalFileName(URL url) throws IOException {
        String originalName = "";
        URLConnection connection = url.openConnection();
        String contentDisposition = connection.getHeaderField("Content-Disposition");

        if (contentDisposition != null && contentDisposition.contains("filename=\"")) {
            originalName = contentDisposition.substring(contentDisposition.indexOf("filename=\"") + 10, contentDisposition.length() - 1);    // The last index is "
        }

        return originalName;
    }

    public void downloadFile(String link, String path) throws IOException {
        System.out.println("Thread " + Thread.currentThread().getId());

        org.apache.http.client.HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(link);
        HttpResponse response = httpClient.execute(httpget);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            String contentDisposition = response.getHeaders("Content-Disposition")[0].getValue();
            String originalName = contentDisposition.substring(contentDisposition.indexOf("filename=\""), contentDisposition.length() - 1);    // The last character is a "
            String extension = originalName.substring(originalName.lastIndexOf('.'), originalName.length());

            InputStream inputStream = entity.getContent();
            File fileToDownload = new File(path + extension);
            if (! fileToDownload.exists()) {
                System.out.println("");

                FileOutputStream fos = new FileOutputStream(fileToDownload);
                int inByte;
                while((inByte = inputStream.read()) != -1) {
                    fos.write(inByte);
                }

                inputStream.close();
                fos.close();
            } else {
                System.out.println(" skipped");
            }
        }
    }

    private class ProcessXML implements Runnable {
        private final File file;

        public ProcessXML(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            try {
                // Transformer transformer = TransformerFactory.newInstance().newTransformer();
                usedID[(int) Thread.currentThread().getId()] = true;

                SAXBuilder saxBuilder = saxBuilders[(int) Thread.currentThread().getId() % saxBuilders.length];

                Document document = saxBuilder.build(file);
                Element root = document.getRootElement();

                String link = root.getChild("info").getChild("url").getValue();
                String path = file.getPath().substring(0, file.getPath().length() - 4);
                System.out.print(file.getName());

//                if (false) {    // Definitely a wrong url
////                    System.out.println("VIEW IT'S VIEW ");
////
////                    System.out.println("");
////
////                    // Fix the xml first
////                    link = link.replace("view", "download");
////                    root.getChild("info").getChild("url").setText(link);    // Write new link to xml
////                    Source input = new JDOMSource(document);
////                    Result output = new StreamResult(file);
////                    transformer.transform(input, output);    // Write it out
////
////                    // And download its document
////                    URL url = new URL(link);
////
////                    String originalName = getOriginalFileName(url);
////                    String extension = originalName.substring(originalName.lastIndexOf('.'), originalName.length());
////                    File fileToDownload = new File(path + extension);
////
////                    FileUtils.copyURLToFile(url, fileToDownload);
//                } else {
////                    URL url = new URL(link);
////
////                    String originalName = getOriginalFileName(url);
////                    String extension = originalName.substring(originalName.lastIndexOf('.'), originalName.length());
////
////                    File fileToDownload = new File(path + extension);
//                    File fileToDownload;
//                    if (link.contains(".doc")) {
//                        fileToDownload = new File(path + ".doc");
//                    } else {
//                        fileToDownload = new File(path + ".pdf");
//                    }
//
//                    if (fileToDownload.exists()) {
//                        System.out.println(" exist, skipped");
//                    } else {
//                        System.out.println("");
//                        FileUtils.copyURLToFile(url, fileToDownload);
////                        downloadFile(link, fileToDownload);
//                    }
//                }

                downloadFile(link, path);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
