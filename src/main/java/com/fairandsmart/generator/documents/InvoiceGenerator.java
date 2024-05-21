package com.fairandsmart.generator.documents;

import com.fairandsmart.generator.documents.layout.ldlc.LDLCLayout;
import com.fairandsmart.generator.documents.data.generator.GenerationContext;
import com.fairandsmart.generator.documents.data.model.InvoiceModel;
import com.fairandsmart.generator.documents.data.model.InvoiceAnnotModel;
import com.fairandsmart.generator.documents.layout.InvoiceLayout;
import com.fairandsmart.generator.documents.layout.amazon.AmazonLayout;
import com.fairandsmart.generator.documents.layout.bdmobilier.BDmobilierLayout;
import com.fairandsmart.generator.documents.layout.cdiscount.CdiscountLayout;
import com.fairandsmart.generator.documents.layout.darty.DartyLayout;
import com.fairandsmart.generator.documents.layout.macomp.MACOMPLayout;
import com.fairandsmart.generator.documents.layout.materielnet.MaterielnetLayout;
import com.fairandsmart.generator.documents.layout.naturedecouvertes.NatureDecouvertesLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
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

public class InvoiceGenerator {

    private static final String DATE_FORMAT = "mm/dd/yyyy hh:mm";

    private static class InvoiceGeneratorHolder {
        private final static InvoiceGenerator instance = new InvoiceGenerator();
    }

    public static InvoiceGenerator getInstance() {
        return InvoiceGeneratorHolder.instance;
    }

    private InvoiceGenerator() {
    }

    public void generateInvoice(InvoiceLayout layout, InvoiceModel model, Path pdf, Path xml, Path img, Path json) throws Exception {

        OutputStream xmlos = Files.newOutputStream(xml);
        XMLStreamWriter xmlOut = XMLOutputFactory.newInstance().createXMLStreamWriter(new OutputStreamWriter(xmlos, StandardCharsets.UTF_8));
        xmlOut.writeStartDocument();
        xmlOut.writeStartElement("", "GEDI", "http://lamp.cfar.umd.edu/media/projects/GEDI/");
        xmlOut.writeAttribute("GEDI_version", "2.4");
        xmlOut.writeAttribute("GEDI_date", "07/29/2013");
        xmlOut.writeStartElement("USER");
        xmlOut.writeAttribute("name", "fairandsmartGenerator");
        xmlOut.writeAttribute("date", new SimpleDateFormat(DATE_FORMAT).format(new Date()));
        xmlOut.writeAttribute("dateFormat", DATE_FORMAT);
        xmlOut.writeEndElement();
        xmlOut.writeStartElement("DL_DOCUMENT");
        xmlOut.writeAttribute("src", img.getFileName().toString());
        xmlOut.writeAttribute("NrOfPages", "1");
        xmlOut.writeAttribute("docTag", "xml");

        Boolean areInitFieldsNull = true; // if set to false, empty fields are also exported to json annot

        PDDocument document = new PDDocument();
        InvoiceAnnotModel annot = new InvoiceAnnotModel(areInitFieldsNull);

        // Build invoice layout and populate pdf
        layout.buildInvoice(model, document, xmlOut, annot);

        // Export as PDF
        document.save(pdf.toFile());

        // Export prettified annot annotations object to JSON file
        Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
        FileWriter json_file = new FileWriter(json.toString());
        gsonBuilder.toJson(annot, json_file);
        json_file.flush();
        json_file.close();

        // Export as img
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
        ImageIOUtil.writeImage(bim, img.toString(), 300);

        document.close();

        xmlOut.writeEndElement();
        xmlOut.writeEndElement();
        xmlOut.writeEndDocument();
        xmlOut.close();

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

            GenerationContext ctx = GenerationContext.generate();
            InvoiceModel model = new InvoiceModel.Generator().generate(ctx);
            InvoiceGenerator.getInstance().generateInvoice(availablesLayout.get(i % availablesLayout.size()), model, pdf, xml, img, json);
            System.out.println("current: " + i);
        }
    }

}
