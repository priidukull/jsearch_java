package ee.legal.caselaw.read;


import ee.legal.caselaw.Logger;
import ee.legal.caselaw.Signaling;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReadTextExtract {
    Signaling signaling = Signaling.getInstance();

    public void process(Map event) throws IOException {
        final Integer refId = (Integer) event.get("ref_id");
        final Boolean drink = (Boolean) event.get("drink");
        final String fileName = (String) event.get("file");
        final String parsedText;
        try {
            parsedText = pdftoText(fileName);
        } catch (FileNotFoundException e) {
            Logger.warning("File {} was not found", fileName);
            return;
        }
        HashMap<String, Object > task = new HashMap<String, Object >(){{
            put("ref_id", refId);
            put("drink", drink);
            put("action", "read.text.insert");
            put("val", parsedText);
            put("file", fileName);
        }};
        signaling.enqueue(task);
    }

    /* Code by Santosh Thottingal
     * http://thottingal.in/blog/2009/06/24/pdfbox-extract-text-from-pdf/
     * */
    private static String pdftoText(String fileName) throws IOException {
        PDFParser parser;
        String parsedText = null;;
        PDFTextStripper pdfStripper = null;
        PDDocument pdDoc = null;
        COSDocument cosDoc = null;
        File file = new File(fileName);
        parser = new PDFParser(new FileInputStream(file));
        try {
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            parsedText = pdfStripper.getText(pdDoc);
        } finally {
            try {
                if (cosDoc != null)
                    cosDoc.close();
                if (pdDoc != null)
                    pdDoc.close();
            } catch (Exception e) {
                Logger.error("Error when finalizing pdfToText: {}", Logger.getStackTrace(e));
            }
        }
        return parsedText;
    }
}
