package com.fairandsmart.generator.documents.layout;

import com.fairandsmart.generator.documents.data.generator.GenerationContext;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.layout.amazon.AmazonLayout;
import com.fairandsmart.generator.documents.layout.bdmobilier.BDmobilierLayout;
import com.fairandsmart.generator.documents.layout.cdiscount.CdiscountLayout;
import com.fairandsmart.generator.documents.layout.darty.DartyLayout;
import com.fairandsmart.generator.documents.layout.invoiceSSD.InvoiceSSDLayout;
import com.fairandsmart.generator.documents.layout.ldlc.LDLCLayout;
import com.fairandsmart.generator.documents.layout.macomp.MACOMPLayout;
import com.fairandsmart.generator.documents.layout.materielnet.MaterielnetLayout;
import com.fairandsmart.generator.documents.layout.naturedecouvertes.NatureDecouvertesLayout;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InvoiceSSDGenerator {

    private static final String DATE_FORMAT = "mm/dd/yyyy hh:mm";

    private static class InvoiceGeneratorHolder {
        private final static InvoiceSSDGenerator instance = new InvoiceSSDGenerator();
    }

    public static InvoiceSSDGenerator getInstance() {
        return InvoiceGeneratorHolder.instance;
    }

    private InvoiceSSDGenerator() {
    }

    public void generateInvoice(InvoiceSSDLayout layout, InvoiceModel model, Path pdf, Path xml, Path img, Path json, Path xmlForEvaluation) throws Exception {

        OutputStream xmlos = Files.newOutputStream(xml);
        XMLStreamWriter xmlout = XMLOutputFactory.newInstance().createXMLStreamWriter(new OutputStreamWriter(xmlos, StandardCharsets.UTF_8));
        xmlout.writeStartDocument();
        xmlout.writeStartElement("", "GEDI", "http://lamp.cfar.umd.edu/media/projects/GEDI/");
        xmlout.writeAttribute("GEDI_version", "2.4");
        xmlout.writeAttribute("GEDI_date", "07/29/2013");
        xmlout.writeStartElement("USER");
        xmlout.writeAttribute("name", "fairandsmartGenerator");
        xmlout.writeAttribute("date", new SimpleDateFormat(DATE_FORMAT).format(new Date()));
        xmlout.writeAttribute("dateFormat", DATE_FORMAT);
        xmlout.writeEndElement();
        xmlout.writeStartElement("DL_DOCUMENT");
        xmlout.writeAttribute("src", img.getFileName().toString());
        xmlout.writeAttribute("NrOfPages", "1");
        xmlout.writeAttribute("docTag", "xml");

        ////
        OutputStream xmlosEval = Files.newOutputStream(xmlForEvaluation);
        XMLStreamWriter xmloutEval = XMLOutputFactory.newInstance().createXMLStreamWriter(new OutputStreamWriter(xmlosEval, StandardCharsets.UTF_8));
        xmloutEval.writeStartDocument();
        xmloutEval.writeStartElement("", "GEDI", "http://lamp.cfar.umd.edu/media/projects/GEDI/");
        xmloutEval.writeAttribute("GEDI_version", "2.4");
        xmloutEval.writeAttribute("GEDI_date", "07/29/2013");
        xmloutEval.writeStartElement("USER");
        xmloutEval.writeAttribute("name", "fairandsmartGenerator");
        xmloutEval.writeAttribute("date", new SimpleDateFormat(DATE_FORMAT).format(new Date()));
        xmloutEval.writeAttribute("dateFormat", DATE_FORMAT);
        xmloutEval.writeEndElement();
        xmloutEval.writeStartElement("DL_DOCUMENT");
        xmloutEval.writeAttribute("src", img.getFileName().toString());
        xmloutEval.writeAttribute("NrOfPages", "1");
        xmloutEval.writeAttribute("docTag", "xml");

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
        xmloutEval.writeEndElement();
        xmloutEval.writeEndElement();
        xmloutEval.writeEndDocument();
        xmloutEval.close();

    }

    public static void main(String[] args) throws Exception {
        List<InvoiceLayout> availablesLayout = new ArrayList<>();
        availablesLayout.add(new AmazonLayout());
        availablesLayout.add(new BDmobilierLayout());
        availablesLayout.add(new CdiscountLayout());
        availablesLayout.add(new DartyLayout());
        availablesLayout.add(new LDLCLayout());
        availablesLayout.add(new MACOMPLayout());
        availablesLayout.add(new MaterielnetLayout());
        availablesLayout.add(new NatureDecouvertesLayout());

        Path generated = Paths.get("target/generated/" + args[0]);
        if ( !Files.exists(generated) ) {
            Files.createDirectories(generated);
        }

        int start = Integer.parseInt(args[1]);
        int stop = Integer.parseInt(args[2]);
        for ( int i=start; i<stop; i++) {
            Path pdf = Paths.get("target/generated/" + args[0] + "/basic-"+ i + ".pdf");
            Path xml = Paths.get("target/generated/" + args[0] + "/basic-"+ i + ".xml");
            Path img = Paths.get("target/generated/" + args[0] + "/basic-"+ i + ".jpg");
            Path json = Paths.get("target/generated/" + args[0] + "/basic-"+ i + ".json");
            Path xmlEval = Paths.get("target/generated/" + args[0] + "/basicEval-"+ i + ".jpg");

            GenerationContext ctx = GenerationContext.generate();
            InvoiceModel model = new InvoiceModel.Generator().generate(ctx);
            InvoiceSSDLayout layout = new InvoiceSSDLayout();
            InvoiceSSDGenerator.getInstance().generateInvoice(layout, model, pdf, xml, img, json, xmlEval);
            System.out.println("current: " + i);
        }
    }

}
