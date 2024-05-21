package com.ssdgen.generator.documents;

import com.ssdgen.generator.documents.data.generator.GenerationContext;
import com.ssdgen.generator.documents.data.model.PayslipModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PayslipGenerator {
    private static final String DATE_FORMAT = "mm/dd/yyyy hh:mm";

    private static class PayslipGeneratorHolder {
        private final static PayslipGenerator instance = new PayslipGenerator();
    }

    public static PayslipGenerator getInstance() {
        return PayslipGeneratorHolder.instance;
    }

    private PayslipGenerator() {
    }

    public void generatePayslip(com.ssdgen.generator.documents.layout.payslip.GenericPayslipLayout layout, PayslipModel model, Path pdf, Path xml, Path img, Path xmlForEvaluation) throws Exception {

        Boolean modeEval= true;
        if (xmlForEvaluation == null) modeEval = false;
        OutputStream xmlos = Files.newOutputStream(xml);
        XMLStreamWriter xmlout = XMLOutputFactory.newInstance().createXMLStreamWriter(new OutputStreamWriter(xmlos, StandardCharsets.UTF_8));
        xmlout.writeStartDocument();
        xmlout.writeStartElement("", "GEDI", "http://lamp.cfar.umd.edu/media/projects/GEDI/");
        xmlout.writeAttribute("GEDI_version", "2.4");
        xmlout.writeAttribute("GEDI_date", "07/29/2013");
        xmlout.writeStartElement("USER");
        xmlout.writeAttribute("name", "ssdgenGenerator");
        xmlout.writeAttribute("date", new SimpleDateFormat(DATE_FORMAT).format(new Date()));
        xmlout.writeAttribute("dateFormat", DATE_FORMAT);
        xmlout.writeEndElement();
        xmlout.writeStartElement("DL_DOCUMENT");
        xmlout.writeAttribute("src", img.getFileName().toString());
        xmlout.writeAttribute("NrOfPages", "1");
        xmlout.writeAttribute("docTag", "xml");

        ////
        OutputStream xmlosEval ;
        XMLStreamWriter xmloutEval = null;
        if (modeEval) {
            xmlosEval = Files.newOutputStream(xmlForEvaluation);
            xmloutEval = XMLOutputFactory.newInstance().createXMLStreamWriter(new OutputStreamWriter(xmlosEval, StandardCharsets.UTF_8));
            xmloutEval.writeStartDocument();
            xmloutEval.writeStartElement("", "GEDI", "http://lamp.cfar.umd.edu/media/projects/GEDI/");
            xmloutEval.writeAttribute("GEDI_version", "2.4");
            xmloutEval.writeAttribute("GEDI_date", "07/29/2013");
            xmloutEval.writeStartElement("USER");
            xmloutEval.writeAttribute("name", "ssdgenGenerator");
            xmloutEval.writeAttribute("date", new SimpleDateFormat(DATE_FORMAT).format(new Date()));
            xmloutEval.writeAttribute("dateFormat", DATE_FORMAT);
            xmloutEval.writeEndElement();
            xmloutEval.writeStartElement("DL_DOCUMENT");
            xmloutEval.writeAttribute("src", img.getFileName().toString());
            xmloutEval.writeAttribute("NrOfPages", "1");
            xmloutEval.writeAttribute("docTag", "xml");
        }

        PDDocument document = new PDDocument();
        layout.builtSSD(model, document, xmlout,xmloutEval);
        document.save(pdf.toFile());

        //Export as img
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
        ImageIOUtil.writeImage(bim, img.toString(), 300);

        document.close();

        xmlout.writeEndElement();
        xmlout.writeEndElement();
        xmlout.writeEndDocument();
        xmlout.close();

        //////
        if (modeEval) {
            xmloutEval.writeEndElement();
            xmloutEval.writeEndElement();
            xmloutEval.writeEndDocument();
            xmloutEval.close();
        }
    }

    public static void main(String[] args) throws Exception {


        Path generated = Paths.get("target/generated/" + args[0]);
        if ( !Files.exists(generated) ) {
            Files.createDirectories(generated);
        }

        int start = Integer.parseInt(args[1]);
        int stop = Integer.parseInt(args[2]);
        for ( int i=start; i<stop; i++) {
            //String ts = "" + System.currentTimeMillis();
            Path pdf = Paths.get("target/generated/" + args[0] + "/basic-"+ i + ".pdf");
            Path xml = Paths.get("target/generated/" + args[0] + "/basic-"+ i + ".xml");
            Path xmlEval = Paths.get("target/generated/" + args[0] + "/basic-"+ i + ".xml");
            Path img = Paths.get("target/generated/" + args[0] + "/basic-"+ i + ".jpg");
            GenerationContext ctx = GenerationContext.generate();
            PayslipModel model = new PayslipModel.Generator().generate(ctx);
            PayslipGenerator.getInstance().generatePayslip(new com.ssdgen.generator.documents.layout.payslip.GenericPayslipLayout(), model, pdf, xml, img,xmlEval);
            System.out.println("current: " + i);
        }
    }
}
